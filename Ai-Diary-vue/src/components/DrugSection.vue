<script setup>
import {ref, reactive, defineEmits, watch} from 'vue';

const props = defineProps({
  drugUsingType: {
    type: String,
    default: 'N'
  }
});

const emit = defineEmits(['updateDrugInfo']);

const selectedDrugs = ref([]);
const statusMap = ref({});
const drugtData = reactive({
  times: {morning: "", lunch: "", evening: ""}, // 오타 수정: morging → morning
  content: "",
  drugType: "Y",
  drugStatus: true,
  selectedDrugs: [],
  sleepDrugs: "",
  drugMorning: "",
  drugLunch: "",
  drugDinner: "",
});

const toggleDrug = (time) => {
  // 선택 토글 기능 만들면 여기!
};

//  입력값이 바뀔 때마다 부모에 emit!
watch(
    () => [drugtData.drugMorning, drugtData.drugLunch, drugtData.drugDinner],
    () => {
      emit('updateDrugInfo', {
        drugMorning: drugtData.drugMorning,
        drugLunch: drugtData.drugLunch,
        drugDinner: drugtData.drugDinner
      });
    },
    {deep: true}
);
</script>

<template>
  <div class="drug-select-swiper" v-if="drugUsingType === 'Y'">
    <h3>💊 약 복용</h3>
    <p>하루에 먹은 약과 복용 시간을 기록합니다.</p>

    <div>
      <label>아침</label>
      <input type="text" v-model="drugtData.drugMorning" placeholder="복용한 아침 약"/>
    </div>
    <div>
      <label>점심</label>
      <input type="text" v-model="drugtData.drugLunch" placeholder="복용한 점심 약"/>
    </div>
    <div>
      <label>저녁</label>
      <input type="text" v-model="drugtData.drugDinner" placeholder="복용한 저녁 약"/>
    </div>
  </div>
</template>
