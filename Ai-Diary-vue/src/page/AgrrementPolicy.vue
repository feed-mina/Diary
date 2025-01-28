<script>
import { ref } from 'vue';

export default {
  name: 'AgreementPolicy',
  setup() {
    // 약관 동의 상태
    const allAgree = ref(false);
    const requiredAgreements = ref([
      { id: 1, text: '[필수] 이용약관', agreed: false },
      { id: 2, text: '[필수] 전자금융거래 이용약관', agreed: false },
      { id: 3, text: '[필수] 개인정보 수집동의서', agreed: false },
    ]);

    const optionalAgreements = ref([
      { id: 4, text: '[선택] 개인정보 수집동의서', agreed: false },
      { id: 5, text: '[선택] 위치기반서비스 이용약관', agreed: false },
    ]);

    // 전체 동의 토글
    const toggleAllAgree = () => {
      const isAgree = allAgree.value;
      requiredAgreements.value.forEach((agreement) => (agreement.agreed = isAgree));
      optionalAgreements.value.forEach((agreement) => (agreement.agreed = isAgree));
    };

    // 필수 약관 체크
    const isAllRequiredAgreed = () => {
      return requiredAgreements.value.every((agreement) => agreement.agreed);
    };

    // 개별 약관 동의 변경
    const toggleAgreement = () => {
      allAgree.value =
        isAllRequiredAgreed() &&
        optionalAgreements.value.every((agreement) => agreement.agreed);
    };

    return {
      allAgree,
      requiredAgreements,
      optionalAgreements,
      toggleAllAgree,
      toggleAgreement,
    };
  },
};
</script>

<template>
  <div class="agreement-policy">
    <h2>약관 동의</h2>
    <!-- 전체 동의 -->
    <div class="agreement-item">
      <label>
        <input type="checkbox" v-model="allAgree" @change="toggleAllAgree" />
        약관 전체 동의
      </label>
    </div>

    <!-- 필수 동의 항목 -->
    <h3>필수 동의 항목</h3>
    <div v-for="agreement in requiredAgreements" :key="agreement.id" class="agreement-item">
      <label>
        <input
          type="checkbox"
          v-model="agreement.agreed"
          @change="toggleAgreement"
        />
        {{ agreement.text }}
      </label>
    </div>

    <!-- 선택 동의 항목 -->
    <h3>선택 동의 항목</h3>
    <div v-for="agreement in optionalAgreements" :key="agreement.id" class="agreement-item">
      <label>
        <input
          type="checkbox"
          v-model="agreement.agreed"
          @change="toggleAgreement"
        />
        {{ agreement.text }}
      </label>
    </div>

    <!-- 다음 단계 버튼 -->
    <button
      :disabled="!isAllRequiredAgreed()"
      class="next-button"
    >
      다음 단계
    </button>
  </div>
</template>

<style scoped>
.agreement-policy {
  padding: 20px;
  max-width: 500px;
  margin: auto;
  font-family: Arial, sans-serif;
}

.agreement-item {
  margin-bottom: 10px;
}

h3 {
  margin-top: 20px;
}

.next-button {
  width: 100%;
  padding: 10px;
  background-color: #4caf50;
  color: white;
  border: none;
  border-radius: 5px;
  font-size: 16px;
  cursor: pointer;
  margin-top: 20px;
  transition: background-color 0.3s;
}

.next-button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}
</style>
