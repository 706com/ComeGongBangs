  <template>
  <div class="PageFavoriteListDesktop w-full pl-6">
    <p class="flex items-center headline4-bold-small mb-[36px]">관심</p>
    <!-----------------------------------------탭 부분------------------------------------------------------->
    <!-- BaseTabsComponent 추가 -->
    <BaseTabsComponent
      :tabs="tabs"
      :activeTab="activeTab"
      @update:activeTab="handleTabClick"
    />

    <!-------------------------------------각 탭의 하단 내용-------------------------------------------------------->
    <div v-if="activeTab === 0" class="tab-content">
      <ProductListComponent
        v-if="likesStore.productList.length > 0"
        :productList="likesStore.productList"
      />
      <EmptyContentComponent v-else :content="'찜한 상품이 없습니다.'" />
    </div>
    <div v-else-if="activeTab === 1" class="tab-content">
      <!-- 팔로우하는 작가 내용 -->
      <FollowAtelierListComponent
        v-if="followStore.followList.length > 0"
        :followList="followStore.followList"
      />
      <EmptyContentComponent v-else :content="'팔로우한 작가가 없습니다.'" />
    </div>
    <!-- 최근본작품 X -->
    <!-- <div v-else-if="activeTab === 2" class="tab-content">
      최근 본 작품 내용
      <EmptyContentComponent :content="'최근 본 상품이 없습니다.'" />
    </div> -->
  </div>
</template>

  <script>
import { defineComponent, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useSideBarStore } from "@/stores/useSidebarStore";
import { useLikesStore } from "@/stores/useLikesStore";
import ProductListComponent from "../product/ProductList4LayoutComponent.vue";
import BaseTabsComponent from "./BaseTabComponent.vue";
import { useFollowStore } from "@/stores/useFollowStore";
import FollowAtelierListComponent from "./FollowAtelierListComponent.vue";
import EmptyContentComponent from "../common/EmptyContentComponent.vue";

export default defineComponent({
  name: "MyFavoriteListComponent",
  components: {
    ProductListComponent,
    FollowAtelierListComponent,
    BaseTabsComponent,
    EmptyContentComponent,
  },
  props: {
    initialTab: {
      type: Number,
      default: 0,
    },
  },

  setup(props) {
    const sideBarStore = useSideBarStore();
    const likesStore = useLikesStore();
    const followStore = useFollowStore();
    const router = useRouter();

    const tabs = [
      { id: 1, title: "찜한 작품", path: "/mypage/favorite/likes" },
      {
        id: 2,
        title: "팔로우하는 작가",
        path: "/mypage/favorite/follow-artist",
      },
      // { id: 3, title: "최근 본 작품", path: "/mypage/favorite/recent-view" }
    ];

    const activeTab = computed(() => sideBarStore.activeTab);

    //찜한 상품리스트 가져오기
    const likedProducts = computed(() => likesStore.likedProducts);
    // followList를 computed로 만들어서 리액티브 상태 확인
    // const followList = computed(() => followStore.followList);

    //탭클릭시 탭변경함수
    const handleTabClick = (index) => {
      sideBarStore.setActiveTab(index); // 사이드바 스토어 업데이트
      router.push(tabs[index].path); // 경로 변경
    };

    // 컴포넌트가 마운트될 때 찜한 상품 리스트 가져오기
    onMounted(() => {
      likesStore.getLikedProductsList();
      followStore.fetchFollow();
    });

    // 라우트가 바뀔 때 `activeTab` 동기화
    sideBarStore.setActiveTab(props.initialTab);

    return {
      tabs,
      activeTab,
      handleTabClick,
      likedProducts, // likedProducts 추가
      likesStore,
      // followList,
      followStore,
    };
  },
});
</script>

  <style>
</style>