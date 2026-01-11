

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

function toggleTime(hour) {
  const time = timeList.find(t => t.hour === hour)
  console.log("@@@@@@TimeSelect time"+time);

  if (!time?.available) return
  if (selectedTimes.value.includes(hour)) {
    selectedTimes.value = selectedTimes.value.filter(t => t !== hour)
  } else {
    selectedTimes.value.push(hour)
  }

  emit('updateTimes', selectedTimes.value)
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
          class="time-slide"
          :class="{
      disabled: !item.available,
      selected: selectedTimes.includes(item.hour)
    }"
          @click="toggleTime(item.hour)"
      >
        <span class="hour">{{ item.hour }}시</span>
<!--        <span class="price">{{ item.price.toLocaleString() }}원</span>-->
            </SwiperSlide>
          </Swiper>
    <div class="time_legend">
      <span class="box time_yellow"></span> wake
      <span class="box time_purple"></span> sleep
<!--      <span class="box time_gray" ></span> -->
    </div>
  </div>
</template>