<template>
  <div
      data-v-14aa7a1c=""
      class="pt-[16px] px-[16px] pb-[20px] border-b-4 border-[#f5f5f5]"
      v-for="(item, index) in reviewStore.writableReviewList" :key="index"
  >
    <div data-v-14aa7a1c="" class="flex justify-between items-center mt-[12px]">
      <a
          data-v-14aa7a1c=""
          href=""
          rel="noopener noreferrer"
          class="flex w-[700px] items-center"
          @click.prevent="openReviewModalClick"
      >
        <div data-v-14aa7a1c="" class="min-w-[76px] min-h-[76px] mr-[8px]">
          <div
              data-v-eee6c6ce=""
              data-v-14aa7a1c=""
              class="BaseProductCardImage BaseProductCardImage__size--large"
              style="--product-card-round: 8"
          >
            <div data-v-eee6c6ce="" class="BaseProductCardImage__image">
              <div
                  data-v-db6621f4=""
                  data-v-eee6c6ce=""
                  class="CoreImageRatio__loadStatus--success CoreImageRatio"
                  radius="8"
                  style="
                  --ids-image-ratio: 1;
                "
                  :style="{'--ids-image-background': `url(${item.productThumbnail})`}"
              >
                <div
                    data-v-db6621f4=""
                    class="CoreImageRatio__image"
                    role="img"
                    alt=""
                ></div>
                <div
                    data-v-dfe5a850=""
                    data-v-db6621f4=""
                    class="CoreImageDefault CoreImageDefault__display--none"
                    style="--ids-image-default-size: 100%"
                >
                  <svg
                      data-v-6d2bd019=""
                      data-v-dfe5a850=""
                      width="24"
                      height="24"
                      viewBox="0 0 24 24"
                      xmlns="http://www.w3.org/2000/svg"
                      class="BaseIcon CoreImageDefault__icon"
                      style="
                      width: 30%;
                      height: 30%;
                      opacity: 1;
                      fill: currentcolor;
                      --BaseIcon-color: #ffffff;
                    "
                  >
                    <path
                        d="M9.98878 22C8.00515 22 6.20082 21.0922 4.92883 19.4213C3.54252 17.599 3.12786 15.0371 3.84847 12.7341C4.77753 9.75972 7.76195 7.01737 12.3355 7.59565C12.8174 7.65617 13.2993 7.74919 13.7745 7.87134V2H17.2229V9.35067C18.6148 10.1553 19.879 11.0721 21.0008 12.0919L18.9252 14.748C18.4209 14.275 17.6925 13.6911 17.1164 13.2776C17.0761 13.9803 16.9808 14.7659 16.8576 15.4641C16.5594 17.153 15.8254 18.7903 14.6184 20.0253C13.3655 21.3063 11.7797 21.9989 9.9899 21.9989L9.98878 22ZM11.1297 10.8883C8.12842 10.8883 7.23186 13.2361 7.07385 13.7416C6.59531 15.2724 7.04135 16.6262 7.61738 17.3838C8.03653 17.9341 8.96895 18.8239 10.4942 18.5875C12.1786 18.3263 13.2489 16.5388 13.5425 14.8757C13.7353 13.7842 13.8115 12.6803 13.8417 11.415C12.9867 11.0945 12.0453 10.8883 11.1297 10.8883Z"
                    ></path>
                  </svg>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div data-v-14aa7a1c="">
          <div
              data-v-14aa7a1c=""
              class="subtitle3-regular-small gray-999--text mb-[4px] text-left"
          >
            {{item.atelierName}}
          </div>
          <div
              data-v-14aa7a1c=""
              class="subtitle3-regular-small line-clamp-1 mb-[4px] text-left"
          >
            {{ item.productName }}
          </div>
          <div
              data-v-14aa7a1c=""
              class="body2-regular-small gray-999--text line-clamp-1 text-left"
          >
            {{ item.optionString }}
          </div>
        </div>
      </a>
      <div
          data-v-b6faa6c8=""
          data-v-14aa7a1c=""
          class="IDSTooltip__wrapper IDSTooltip__display--hide IDSTooltip__colorType--gray IDSTooltip__innerPosition--top IDSTooltip__innerAlign--center IDSTooltip__innerCloseAlign--center"
          style="--ids-tooltip-full-left: --8px; --ids-tooltip-offset-y: 16px"
      >
        <div data-v-b6faa6c8="" class="IDSTooltip__trigger">
          <button
              data-v-524f63ea=""
              data-v-7940d6dd=""
              data-v-14aa7a1c=""
              type="outline"
              class="CoreButton BaseButtonRectangle subtitle3-bold-small BaseButtonRectangle__outline"
              style="
              background-color: rgb(255, 255, 255);
              color: #000;
              height: 44px;
              width: 200px;
              flex-direction: row;
              --core-button-padding-x: 16;
              --button-rectangle-border-color: #000;
            "
              @click="openReviewModalClick(item)"
          >
            <div data-v-524f63ea="" class="inline-flex items-center">
              <span data-v-524f63ea="" class="CoreButton__text"
              >후기 작성하기</span
              >
            </div>
          </button>
        </div>
      </div>
    </div>
  </div>
  <ReviewModalComponent
      v-if="isReviewModalOpen"
      @close="closeReviewModalClick"
  />
</template>

<script>
import ReviewModalComponent from "./ReviewModalComponent.vue";
import { mapStores } from "pinia";
import { useReviewStore } from "@/stores/useReviewStore";

export default {
  name: "WritableReviewComponent",
  computed: {
    ...mapStores(useReviewStore),
  },
  created() {
    this.getWritableReviewList(0,10);
  },
  data() {
    return {
      isReviewModalOpen: false,
    };
  },
  components: {
    ReviewModalComponent,
  },
  methods: {
    openReviewModalClick(item) {
      this.reviewStore.productOfWritingReview=item;
      this.isReviewModalOpen = !this.isReviewModalOpen;
    },
    closeReviewModalClick() {
      this.isReviewModalOpen = false;
    },
    async getWritableReviewList(page, size){
      await this.reviewStore.getWritableReviewList(page,size);
    }
  },
};
</script>
<style>
.text-left{
  text-align: left;
}
</style>

