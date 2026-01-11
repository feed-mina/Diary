

<script setup>
import { ref, defineEmits, watch  } from 'vue'

import { Swiper, SwiperSlide } from 'swiper/vue'
import { Navigation } from 'swiper/modules'
import 'swiper/css'
import 'swiper/css/navigation'
import 'swiper/swiper-bundle.css'
const emit = defineEmits(['updateTimes'])
const timeList = Array.from({ length: 25 }, (_, i) => ({ hour: i, available: true }))

const selectedTimes = ref([]);
const statusMap = ref({});


const toggleTime = (hour) => {
  if (selectedTimes.value.length === 0 && hour === 0) {
    // 처음 클릭이면서 0시일 때 → 0~4시 자동 선택

    console.log("@@@ selectedTimes " + selectedTimes);
  } else {
    const index = selectedTimes.value.indexOf(hour);
    console.log("@@@ index " + index);
    if (index === -1) {
      selectedTimes.value.push(hour);
    } else {
      selectedTimes.value.splice(index, 1);
    }
    selectedTimes.value.sort((a, b) => a - b);
  }
  updateSleepStatus();
  emit('updateTimes', selectedTimes.value)
};

const updateSleepStatus = () => {
  const sorted = [...selectedTimes.value].sort((a, b) => a - b);
  statusMap.value = {};

  console.log("@@@ sorted " + sorted);
    for (let i = 0; i < sorted.length; i++) {
      const current = sorted[i];
      const prev = sorted[i - 1];
      const next = sorted[i + 1];

      const hasPrev = prev === current - 1;
      const hasNext = next === current + 1;

      if (hasPrev || hasNext) {
        statusMap.value[current] = 'sleep';  // 앞이나 뒤 중 하나라도 붙어있으면 sleep
      } else {
        statusMap.value[current] = 'wake';   // 혼자 떨어진 경우는 wake
      }
      console.log("@@@ statusMap " + statusMap.value);
      console.log("@@@ statusMap.value[current] " + statusMap.value[current]);
    }
  };

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
        :slides-per-view="6"
        class="mySwiper"
    >
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
