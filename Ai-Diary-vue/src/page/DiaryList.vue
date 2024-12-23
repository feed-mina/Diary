<script>
import {ref, onMounted} from 'vue';
import { useRouter } from 'vue-router';
import axios from "axios";
import Cookies from "universal-cookie";
import Modal from '../components/Modal.vue';
import styled from '@vue-styled-components/core';
export default {
  name: 'DiaryList', // 다중 단어 이름으로 변경
  components : { Modal},
  setup(){
    const router = useRouter();
    const cookies = new Cookies();
    const diaryList = ref([]);
    const userId = ref("");
    const page = ref({
      page : 1,
      totalPage : 0,
    });
    const modalOpen = ref(false);
    const openModal = () =>{
      modalOpen.value = true;
    };
    const closeModal = () => {
      modalOpen.value = false;
    };

    const getDiaryList = async(id, temPage) => {
      try{
        const response = await axios.get(`localhost:8080/api/diary/getOtherList/${id}?page=${temPage}&perPage=6`,
          {
            headers : {
              accessToken : cookies.get("userData").accessToken,
            },
          }
        );
        diaryList.value = response.data.diary;
        page.value = {
          page: temPage,
          totalPage : response.data.totalPage,
        };

      } catch(e){
        console.error(e);
        router.push("/");
      }
    };

    const onClickPagination = async(newPage) =>{
      page.value.page = newPage;
      await getDiaryList(userId.value, newPage);
    };

    const navigateTo = (path) =>{
      router.push(path);
    };

    onMounted(() => {
      const userData = cookies.get("userData");
      if(!userData){
        router.push("/");
      } else{
        userId.value = userData.id;
        getDiaryList(userId.value, page.value.page);
      }
    });

return{
  diaryList,
  page,
  modalOpen,
  openModal,
  closeModal,
  onClickPagination,
  navigateTo
};

  },

};
</script>

<template>

<div class="diaryList">
  <div class="diaryList_content">
    <main class="diaryOtherList">
      <div v-if="diaryList && diaryList.length > 0">
        <div v-for="(it, index) in diaryList" :key="it.shortId">
          <div class="mini-posts">
          <article class="mini-post">
            <header>
              <h3>
                <a class="mini-post-title" @click="navitateTo(`/diary/${it.shortId}/diaryview`)">
                  <span style="font-size: bold; color:#604e2e">
                    &nbsp; {{ it.author }} &nbsp;
                  </span>
                  <span>&nbsp; {{ it.title.substring(0, 7) }}...</span>
                </a>
              </h3>
              <time class="published" :dateTime="it.created_at">
                {{ it.createdDate.substring(0, it.createdDate.length / 2) }}
              </time>
              <a @click="navigateTo(`/diary/${it.shortId}/diaryView`)">
                <h3>DiaryList</h3>
              </a>
            </header>
            <a @click="navigateTo(`/diary/${it.shortId}/diaryView`)" class="image">
              <img :src="`data:image/jpeg;base64,${it.img_url}`" alt="" style="width: 100%; overflow: hidden"/>
            </a>
          </article>

        </div>

        </div>
      </div>
      <div v-else>아직 게시글이 없습니다.😢</div>
    </main>
    <div style="text-align:center" class="diaryOtherList_ul">
      <nav aria-label="Page navigation example" style="display:inline-block">
        <ul>
          <li v-if="page.page > 1">
            <a aria-label="Previous" @click="onClickPagination(page.page - 1)">
              &laquo;
            </a>
          </li>
          <li>
            <a @click="onClickPagination(page.page)">
              {{ page.page }}
            </a>
          </li>
          <li v-if="page.page < page.totalPage">
              <a @click="onClickPagination(page.page + 1)">
                {{ page.page + 1 }}
              </a>
          </li>
          <li v-if="page.page < page.totalPage">
            <a aria-label="Next" @click="onClickPagination(page.page + 1)">
              &raquo;
            </a>
          </li>
        </ul>
      </nav>
    </div>
    <div class="DiaryRabbitKV">
      <div class="DiaryRabbitButton" @click="openModal">
        <h3>모달 OPEN</h3>
      </div>
      <Modal :open="modalOpen" :close="closeModal" header="Diary List" />
    </div>
  </div>
</div>

</template>


<style scoped>
.diaryOtherList_paper {
  width: 100%;
  min-width: 400px;
  height: 100%;
  margin: 0 auto;
  border-radius: 2em;
  overflow: hidden;
}

.diaryOtherList_paper_content {
  height: 100%;
  width: 97%;
  background: linear-gradient(transparent, transparent 28px, #eee7db 28px);
  background-size: 30px 30px;
  display: flex;
  flex-direction: column;
}

.diaryOtherList {
  padding: 1.875em;
  padding-top: 1.5em;
  width: 100%;
  height: 87%;
  z-index: 9999;
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-start;
  align-items: center;
}

.diaryOtherList_ul ul {
  list-style-type: none;
  float: left;
  margin-left: 10px;
  cursor: pointer;
  display: flex;
  flex-direction: row;
  justify-content: flex-start;
  align-items: center;
}

.diaryOtherList_ul li {
  margin: 10px;
}

.diaryOtherList_ul li:hover {
  transform: scale(1.5);
  transition: transform 0.2s ease-out;
}

.mini-post {
  display: flex;
  flex-direction: column-reverse;
  background: #ffffff;
  border: solid 1px #c1ab86;
  border-radius: 0.25em;
  padding: 2vmin;
  margin: 2vmin;
  width: 30vmin;
  height: 33vmin;
}

.mini-post-title {
  display: flex;
  flex-direction: row;
  justify-content: flex-start;
}

.mini-post a {
  cursor: pointer;
}

.mini-post header {
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  padding: 1.25em 4.25em 0.1em 1.25em;
  min-height: 4em;
  position: relative;
  flex-grow: 1;
}

.mini-post header h3 {
  font-size: 0.7em;
  margin: 0;
  margin-bottom: 2em;
}

.mini-post header .published {
  font-size: 0.6em;
  font-weight: 400;
  letter-spacing: 0.25em;
  margin: -0.625em 0 1.7em 0;
  text-transform: uppercase;
  text-align: left;
}

.mini-post header .author {
  position: absolute;
  right: 2em;
  top: 2em;
}

.mini-post .image {
  overflow: hidden;
  width: 99%;
  border-radius: 0.25em;
  cursor: pointer;
}

.mini-post .image img {
  transition: transform 0.2s ease-out;
  width: 100%;
}

.mini-post .image:hover img {
  transform: scale(1.05);
}

.mini-post .author img {
  margin-right: -20px;
  border-radius: 100%;
  width: 2em;
}

.mini-post .image {
  border: 1px solid #c1ab86;
}
</style>
