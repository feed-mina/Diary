<script>
import { mapState, mapActions } from 'vuex';
import {ref, onMounted} from 'vue';
import { useRouter } from 'vue-router';
import axios from "axios";
import Cookies from "universal-cookie";
import styled from '@vue-styled-components/core';

export default {
  name: 'ComponentB',
  setup(){
    const router = useRouter();
    const cookies = new Cookies();
    const jwtToken = cookies.get("jwt")?.jwt; // 쿠키에서 jwt 속성 가져오기
       
    const diaryList = ref([]);
    const diaries = ref([]); // diaries를 반응형으로 선언

    const page = ref({
      pageNo: 1,
      pageSize: 10,
    });
    
    const getDiaryList = async(userId, pageNo) =>{
      try{
        const response = await axios.get("http://localhost:8080/api/diary/viewDiaryList",
         {
          headers : {
            Authorization: `Bearer ${jwtToken}`,
            "Content-Type": "application/json",
            "X-Forwarded-For": "127.0.0.1",
          },
          params:{
            userId : userId,
            pageNo : page.value.pageNo,
            pageSize : page.value.pageSize,
          },
          withCredentials: true, // 쿠키 인증 허용
        });
        console.log("response : ",response);

        const { list,  pageSize, pageNum, total } = response.data.diaryList;
        
        // 다이어리 리스트 업데이트
        diaryList.value = list;
        diaries.value = list; // diaries 업데이트

        console.log("diaries : ", diaries);
        page.value = {
          pageNo : pageNum,
          pageSize : pageSize,
          total : total,
        }
        console.log("page : ",page);
      
      }catch(error){
        console.error("Error fetching diary list: ",error);
//        router.push("/");
      }
    };

    const onClickPagination = async(newPage) =>{
      page.value.pageNo = newPage;
      console.log("newPage : ", newPage);
      const userId = localStorage.getItem("userId");

      await getDiaryList(userId, newPage);
    };

    onMounted(()=>{
      const userId = localStorage.getItem("userId");
      console.log("userId : ", userId);
      if(!userId){
        router.push("/");
      } else{
        getDiaryList(userId, page.value.pageNo);
      }
    });
  
    return{
      diaries,
      diaryList,
      page,
      onClickPagination
    };
  
  },
};
</script>


<template>
  <div class="diaryList">
    <h1>ComponentB, 일기장</h1>
    <div class="diaryList_content">
      <main class="diaryOtherList">
        <div v-if="diaries.length > 0">
          <div v-for="(it, index) in diaries" :key="it.diaryId">
            <h1>{{ diaries.length }}</h1>
            <div class="diary-post">
              <header>
                <h3>
                    {{ it.author || '익명' }}
                </h3>
                <span>
                  &nbsp;{{ it.title ? it.title.substring(0,10) : '제목 없음' }}...
                </span>
                <time class="published" :dateTime="it.regDt">
                  {{ new Date(it.regDt || it.date).toLocaleDateString() }}
                </time>
              </header>
              <p>{{  it.content ? it.content.substring(0,50) : '내용 없음' }}</p>
            </div>
          </div>
        </div>
        <div v-else>아직 게시글이 없습니다.</div>
      </main>
        <!--페이지네이션-->
        <div class="pagination" v-if="page.total > page.pageSize">
          <button v-for="p in Math.ceil(page.total / page.pageSize)" :key="p" @click="onClickPagination(p)">
            {{ p }}
          </button>
        </div>
    </div>
  </div>
</template>

