import { createRouter, createWebHistory } from "vue-router";
import Home from "@/page/Home.vue";
import About from "@/page/About.vue";
import NotFound from "@/page/NotFound.vue";
import ComponentA from "@/components/ComponentA.vue";
import ComponentB from "@/components/ComponentB.vue";
import ComponentC from "@/components/ComponentC.vue";
import Tutorial from "@/page/Tutorial.vue";
import Signin from "@/page/Signin.vue";
import LoginPage from "@/page/LoginPage.vue";
import SignupPage from "@/page/SignupPage.vue";
import MemberOutPage from "@/page/MemberOutPage.vue";
import AgrrementPolicy from "@/page/AgrrementPolicy.vue";
import DiaryWriting from "@/page/DiaryWriting.vue";
import DiaryView from "@/page/DiaryView.vue";
import DiaryList from "@/page/DiaryList.vue";
import EditPassword from "@/page/EditPassword.vue";
import ConfirmPassword from "@/page/ConfirmPassword.vue";
import ConfirmPassword2 from "@/page/ConfirmPassword2.vue";
import EditNewPassword from "@/page/EditNewPassword.vue";

const routes = [
  { path: "/", name: "Home", component: Home }, 
  { path: "/about", name: "About", component: About },
  { path: "/notFound", name: "NotFound", component: NotFound },
  { path: "/componentA", name: "ComponentA", component: ComponentA },
  { path: "/componentB", name: "ComponentB", component: ComponentB },
  { path: "/componentC", name: "ComponentC", component: ComponentC },
  { path: "/diary/tutorial", name: "Tutorial", component: Tutorial },
  { path: "/signin", name: "Signin", component: Signin },
  { path: "/login", name: "LoginPage", component: LoginPage },
  { path: "/signup", name: "SignupPage", component: SignupPage },
  { path: "/memberOut", name: "MemberOutPage", component: MemberOutPage }, 
  { path: "/agreementPolicy", name: "AgrrementPolicy", component: AgrrementPolicy },
  {path:"/diary/write", name:"DiaryWriting",component:DiaryWriting},
  {path:"/diary/view/:diaryId", name:"DiaryView",component:DiaryView},
  {path:"/diary/common", name:"DiaryList",component:DiaryList},
  { path: "/mypage/confirmPassword", name: "ConfirmPassword", component: ConfirmPassword }, 
  { path: "/mypage/confirmPassword2", name: "ConfirmPassword2", component: ConfirmPassword2 }, 
  { path: "/edit/newPassword", name: "EditNewPassword", component:EditNewPassword}, //EditNewPassword.vue
  { },
  { path: "/edit/password", name: "EditPassword", component: EditPassword }, 
  {
    path: "/:pathMatch(.*)*",
    name: "CatchAll",
    component: NotFound,
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
