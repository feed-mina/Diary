# app/main.py
from pathlib import Path
import os
import time
import logging
import httpx
from dotenv import load_dotenv
from pydantic import BaseModel
from fastapi import FastAPI
from konlpy.tag import Okt
from transformers import pipeline
from googletrans import Translator
from google.cloud import texttospeech
from gtts import gTTS
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles
from fastapi.responses import StreamingResponse
import io
import threading
from fastapi.responses import Response

# 로그 설정
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# .env 파일 불러오기
env_path = Path(__file__).resolve().parent.parent / ".env"
load_dotenv(dotenv_path=env_path)
os.environ["GOOGLE_APPLICATION_CREDENTIALS"] = "C:/Users/leeyu/Project/speak_diary/pronounce-api/adroit-flare-458213-j4-e36e1e1e5502.json"
DOMAIN_NAME = os.getenv("DOMAIN_NAME", "http://localhost:8001")

HF_API_URL_KO_EN = "https://api-inference.huggingface.co/models/Helsinki-NLP/opus-mt-ko-en"
HF_API_URL_EN_JA = "https://api-inference.huggingface.co/models/Helsinki-NLP/opus-mt-en-jap"

HF_API_KEY = os.getenv("HF_API_KEY")

app = FastAPI()

origins = [
    "http://localhost:4000",
    "http://127.0.0.1:4000",
    "http://localhost:8080",
    "http://127.0.0.1:8080",
    "https://justsaying.co.kr"
]

# 이 줄 추가! (static 폴더 연결)
app.mount("/static", StaticFiles(directory="static"), name="static")
# CORS 설정 (Vue랑 통신할 때 꼭 필요!)

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

okt = Okt()

# pydantic 모델
class Diary(BaseModel):
    content: str

@app.get("/")
async def root():
    logger.info("/root 호출됨")
    return {"message": "FastAPI 서버가 열렸어요!"}

@app.post("/analyze")
async def analyze_text(diary: Diary):
    logger.info("/analyze 호출됨2")
    # 받아온 일기 내용
    text = diary.content
    # 형태소 분석 실행
    tokens = okt.pos(text)

    return {"morph_analysis": tokens}

# 구글 번역기 객체 만들기
translator = Translator()

def delete_file_later(filepath, delay=10):
    def delete():
        time.sleep(delay)
        if os.path.exists(filepath):
            os.remove(filepath)
    threading.Thread(target=delete).start()

@app.post("/translate")
async def translate_text(diary: Diary):
    text = diary.content

    # 1단계: 한국어 ➔ 영어 번역
    english = translator.translate(text, src='ko', dest='en').text

    # 2단계: 영어 ➔ 일본어 번역
    japanese = translator.translate(english, src='en', dest='ja').text

    return {"translated_text": japanese}

async def translate(text: str, url: str):
    headers = {
        "Authorization": f"Bearer {HF_API_KEY}",
        "Content-Type": "application/json"
    }
    async with httpx.AsyncClient(timeout=20.0) as client:
        response = await client.post(url, headers=headers, json={"inputs": text})

        if response.status_code != 200:
            raise Exception(f"HuggingFace API 호출 실패: {response.status_code} {response.text}")

        result = response.json()
        # 에러 처리
        if isinstance(result, dict) and result.get("error"):
            raise Exception(f"HuggingFace API 오류: {result['error']}")

        return result[0]["translation_text"]

# TTS 함수
def text_to_speech(text, lang="ja-JP"):
    client = texttospeech.TextToSpeechClient()

    synthesis_input = texttospeech.SynthesisInput(text=text)

    voice = texttospeech.VoiceSelectionParams(
        language_code=lang,
        ssml_gender=texttospeech.SsmlVoiceGender.NEUTRAL
    )

    audio_config = texttospeech.AudioConfig(
        audio_encoding=texttospeech.AudioEncoding.MP3
    )

    response = client.synthesize_speech(
        input=synthesis_input, voice=voice, audio_config=audio_config
    )

    filename = f"output_{int(time.time())}.mp3"  # 고유 이름 만들기
    filepath = f"static/{filename}"  # static 폴더 안에 저장

    os.makedirs("static", exist_ok=True)  # static 폴더 없으면 만들기

    with open(filepath, "wb") as out:
        out.write(response.audio_content)

    return filename  # 파일 이름만 리턴

class TextRequest(BaseModel):
    text: str

@app.post("/tts_only_test")
async def tts_only(text_request: TextRequest):
    text = text_request.text
    logger.info(f" TTS 요청 받은 텍스트: {text}")

    client = texttospeech.TextToSpeechClient()

    synthesis_input = texttospeech.SynthesisInput(text=text)
    voice = texttospeech.VoiceSelectionParams(
        language_code="ja-JP",
        ssml_gender=texttospeech.SsmlVoiceGender.NEUTRAL
    )
    audio_config = texttospeech.AudioConfig(
        audio_encoding=texttospeech.AudioEncoding.MP3
    )

    response = client.synthesize_speech(
        input=synthesis_input,
        voice=voice,
        audio_config=audio_config
    )
    audio_bytes = response.audio_content
    logger.info("✅ 생성된 음성 데이터 길이: %d 바이트", len(audio_bytes))

    # 메모리에 저장
    # audio_stream = io.BytesIO(response.audio_content)

    audio_bytes = response.audio_content

    audio_stream = io.BytesIO(audio_bytes)

    return StreamingResponse(
        audio_stream,
        media_type="audio/mpeg",
        headers={
            "Content-Disposition": "inline; filename=output.mp3",
            "Content-Length": str(len(audio_bytes)),
        }
    )


@app.post("/tts_only")
async def tts_only(text_request: TextRequest):
    text  = text_request.text
    logger.info(f"📥 TTS 요청 받은 텍스트: {text }")

    filename = text_to_speech(text)
    # 10초 후 자동 삭제
    delete_file_later(f"static/{filename}", delay=10)
    return {"tts_audio_url": f"{DOMAIN_NAME}/static/{filename}"}


@app.post("/translate_only")
async def translate_only(diary: Diary):
    text = diary.content
    english = translator.translate(text, src='ko', dest='en').text
    japanese = translator.translate(english, src='en', dest='ja').text
    return {"translated_text": japanese}

@app.post("/translate_and_tts")
async def translate_and_tts(diary: Diary):
    text = diary.content

    # 1단계: 번역(구글 번역기)
    english = translator.translate(text, src='ko', dest='en').text
    japanese = translator.translate(english, src='en', dest='ja').text

    # 2단계: TTS (Cloud Text-to-Speech API 기준)
    tts_filename = text_to_speech(japanese)

    return {
        "translated_text": japanese,
        "tts_audio_url": f"{DOMAIN_NAME}/static/{tts_filename}"
    }

@app.post("/translate1")
async def translate_text(diary: Diary):
    text = diary.content

    # 1단계: 한국어 ➔ 영어
    english_text = await translate(text, HF_API_URL_KO_EN)

    # 2단계: 영어 ➔ 일본어
    japanese_text = await translate(english_text, HF_API_URL_EN_JA)

    return {"translated_text": japanese_text}


# 모델 불러오기
ko_to_en = pipeline("translation", model="Helsinki-NLP/opus-mt-ko-en")
en_to_ja = pipeline("translation", model="staka/fugumt-en-ja")

@app.post("/translate2")
async def translate2_text(diary: Diary):
    text = diary.content

    # 1단계: 한국어 -> 영어
    english = ko_to_en(text)[0]['translation_text']

    # 2단계: 영어 -> 일본어
    japanese = en_to_ja(english)[0]['translation_text']

    return {"translated_text": japanese}
