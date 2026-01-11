

<script setup>
import { ref, defineEmits,watch } from 'vue'

import { Swiper, SwiperSlide } from 'swiper/vue'
import { Navigation } from 'swiper/modules'
import 'swiper/css'
import 'swiper/css/navigation'
import 'swiper/swiper-bundle.css'
// const emit = defineEmits(['updateTimes'])

const props = defineProps(['modelValue']);
const emit = defineEmits(['update:modelValue'])


const timeList = [
  {hour: 0,  available: true},
  {hour: 1,  available: true},
  {hour: 2,  available: true},
  {hour: 3,  available: true},
  {hour: 4,  available: true},
  {hour: 5,  available: true},
  {hour: 6,  available: true},
  {hour: 7,  available: true},
  {hour: 8,  available: true},
  {hour: 9,  available: true},
  {hour: 10,  available: true},
  {hour: 11,  available: true},
  {hour: 12,  available: true},
  {hour: 13,  available: true},
  {hour: 14,  available: true},
  {hour: 15,  available: true},
  {hour: 16,  available: true},
  {hour: 17,  available: true},
  {hour: 18,  available: true},
  {hour: 19,  available: true},
  {hour: 20,  available: true},
  {hour: 21,  available: true},
  {hour: 22,  available: true},
  {hour: 23,  available: true},
  {hour: 24,  available: true},
]

const selectedTimes = ref([])

const statusMap = ref({})

function toggleTime(hour) {
  const time = timeList.find(t => t.hour === hour)

  if (!time?.available) return;
  if (selectedTimes.value.includes(hour)) {
    // 선택 해제
    selectedTimes.value = selectedTimes.value.filter(t => t !== hour);
    statusMap.value[hour] = null;  // 상태 초기화
  } else {
    // 선택 추가
    selectedTimes.value.push(hour)
    statusMap.value[hour] = 'sleep'  // 자동으로 sleep으로 설정
  }

  emit('update:modelValue', [...selectedTimes.value].sort((a, b) => a - b));
  console.log('selectedTimes:', selectedTimes.value)
  // console.log("@@@ updateTimes " + [...selectedTimes.value].sort((a, b) => a - b));
}


// 예시: 선택 시
const update = (val) => {
  emit('update:modelValue', val)
}

watch(() => props.modelValue, (newVal) => {
  selectedTimes.value = newVal || []
  // console.log('selectedTimes.value',selectedTimes.value);
  // console.log('props.modelValue: ', props.modelValue)
  // console.log('newVal: ',newVal)
})
</script>
<template>
  <div class="time-select-swiper">
    <h3>시간 선택</h3>


    <!-- 화살표 버튼 -->

    <div class="swiper-nav-buttons">
      <div class="swiper-button-prev" style="pading-right:1.8rem; font-weight: 600; font-size: 2rem;"> ← </div>
      <div class="swiper-button-next" style="padding-left: 1.8rem; font-weight: 600; font-size: 2rem;"> → </div>
    </div>

    <Swiper
        :modules="[Navigation]"
        :navigation="{
    nextEl: '.swiper-button-next',
    prevEl: '.swiper-button-prev'
  }"
        class="mySwiper" >

      <SwiperSlide
          v-for="item in timeList"
          :key="item.hour"
      >
        <div
            class="time-slide"
            :class="{
            disabled: !item.available,
            selected: selectedTimes.includes(item.hour),
            'is-sleep': statusMap[item.hour] === 'sleep',
            wake: statusMap[item.hour] === 'wake'
          }"
            @click="toggleTime(item.hour)"
        >
          {{ item.hour }}시
        </div>
      </SwiperSlide>
          </Swiper>
    <div class="time_legend">
      <span class="box time_yellow"></span> wake
      <span class="box time_purple"></span> sleep
    </div>
  </div>
</template>



<style scoped>
.time-slide {
  cursor: pointer;
  padding: 10px;
  margin: 5px;
  border-radius: 10px;
  text-align: center;
  background-color: #f5d36c; /* 기본 노랑 */
  transition: background-color 0.3s;
}

.time-slide.selected {
  font-weight: bold;
}

.time-slide.is-sleep {
  background-color: #cbb2ff !important; /* 보라색 sleep */
}

.time-slide.wake {
  background-color: #f5d36c !important; /* 노랑 wake */
}

.time_legend {
  margin-top: 10px;
}

.box {
  display: inline-block;
  width: 15px;
  height: 15px;
  border-radius: 3px;
  margin-right: 5px;
}

.time_yellow {
  background-color: #f5d36c;
}

.time_purple {
  background-color: #cbb2ff;
}
</style>
