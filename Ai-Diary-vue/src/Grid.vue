<script setup>
import { ref, computed } from 'vue';

// Props 정의 (JavaScript에서는 타입 제거)
const props = defineProps({
  data: Array,
  columns: Array,
  filterKey: String,
});

// 정렬 키와 정렬 순서 설정
const sortKey = ref('');
const sortOrders = ref(
  props.columns.reduce((o, key) => {
    o[key] = 1;
    return o;
  }, {})
);

// 필터 및 정렬된 데이터 계산
const filteredData = computed(() => {
  let { data, filterKey } = props;
  if (filterKey) {
    filterKey = filterKey.toLowerCase();
    data = data.filter((row) => {
      return Object.keys(row).some((key) => {
        return String(row[key]).toLowerCase().includes(filterKey);
      });
    });
  }

  const key = sortKey.value;
  if (key) {
    const order = sortOrders.value[key];
    data = data.slice().sort((a, b) => {
      a = a[key];
      b = b[key];
      return (a === b ? 0 : a > b ? 1 : -1) * order;
    });
  }
  return data;
});

// 정렬 함수
function sortBy(key) {
  sortKey.value = key;
  sortOrders.value[key] *= -1;
}

// 문자열 첫 글자 대문자로 변환
function capitalize(str) {
  return str.charAt(0).toUpperCase() + str.slice(1);
}
</script>

<template>
  <table v-if="filteredData.length">
    <thead>
      <tr>
        <th
          v-for="(key, cIndex) in columns"
          @click="sortBy(key)"
          :class="{ active: sortKey == key }"
          :key="cIndex"
        >
          {{ capitalize(key) }}
        </th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="(entry, index) in filteredData" :key="index">
        <td v-for="(key, colIndex) in columns" :key="colIndex">
          {{ entry[key] }}
        </td>
      </tr>
    </tbody>
  </table>
  <p v-else>No matches found.</p>
</template>

