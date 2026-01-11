

<script setup>
import { ref, defineEmits } from 'vue'

import { Swiper, SwiperSlide } from 'swiper/vue'
import { Navigation } from 'swiper/modules'
import 'swiper/css'
import 'swiper/css/navigation'
import 'swiper/swiper-bundle.css'
const emit = defineEmits(['updateTimes'])

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

  if (!time?.available) return
  if (selectedTimes.value.includes(hour)) {
    // 선택 해제
    selectedTimes.value = selectedTimes.value.filter(t => t !== hour)
  } else {
    selectedTimes.value.push(hour)
  }

  // 시간 정렬
  selectedTimes.value.sort((a, b) => a - b)

  // statusMap 초기화
  statusMap.value = {}

  // 연속된 구간 찾아서 sleep / wake 설정
  let group = []
  const all = selectedTimes.value

  for (let i = 0; i < selectedTimes.value.length; i++) {
    const current = all[i]
    const prev = all[i - 1]
    // 첫 번째 요소거나 연속된 경우
    if (i === 0 || current === prev + 1) {
      group.push(current)
    } else {
      // 이전 그룹 처리
      if (group.length >= 2) {
        group.forEach(h => statusMap.value[h] = 'sleep')
      } else if (group.length === 1) {
        statusMap.value[group[0]] = 'wake'
      }
      group = [current]
    }
  }

  // 마지막 그룹 처리
  if (group.length >= 2) {
    group.forEach(h => statusMap.value[h] = 'sleep')
  }else if (group.length === 1) {
    statusMap.value[group[0]] = 'wake'
  }
  // 부모에게 정렬된 배열 전달
  emit('updateTimes', [...selectedTimes.value])

  console.log("@@@ updateTimes " + [...selectedTimes.value].sort((a, b) => a - b));
}

</script>
<template>
  <div class="time-select-swiper">
    <h3>시간 선택</h3>


    <!-- 화살표 버튼 -->

    <div class="swiper-nav-buttons">
      <div class="swiper-button-prev">←</div>
      <div class="swiper-button-next">→</div>
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

    <p style="margin-top: 10px; font-size: 14px;">
      선택한 시간: {{ selectedTimes.join(', ') }}시
    </p>

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
