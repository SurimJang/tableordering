<template>
  <div>
    <h3 class="text-base font-semibold leading-6 text-gray-900">지난 30일 매출 통계</h3>

    <dl class="mt-5 grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-3">
      <div v-for="item in stats" :key="item.id" class="relative overflow-hidden rounded-lg bg-white px-4 pb-12 pt-5 shadow sm:px-6 sm:pt-6">
        <dt>
          <div class="absolute rounded-md bg-indigo-500 p-3">
            <component :is="item.icon" class="h-6 w-6 text-white" aria-hidden="true" />
          </div>
          <p class="ml-16 truncate text-sm font-medium text-gray-500">{{ item.name }}</p>
        </dt>
        <dd class="ml-16 flex items-baseline pb-6 sm:pb-7">
          <p class="text-2xl font-semibold text-gray-900">{{ item.stat }}</p>
          <p :class="[item.changeType === 'increase' ? 'text-green-600' : 'text-red-600', 'ml-2 flex items-baseline text-sm font-semibold']">
            <ArrowUpIcon v-if="item.changeType === 'increase'" class="h-5 w-5 flex-shrink-0 self-center text-green-500" aria-hidden="true" />
            <ArrowDownIcon v-else class="h-5 w-5 flex-shrink-0 self-center text-red-500" aria-hidden="true" />
            <span class="sr-only"> {{ item.changeType === 'increase' ? 'Increased' : 'Decreased' }} by </span>
            {{ item.change }}
          </p>
          <div class="absolute inset-x-0 bottom-0 bg-gray-50 px-4 py-4 sm:px-6">
            <div class="text-sm">
              <a href="#" class="font-medium text-indigo-600 hover:text-indigo-500"
              >View all<span class="sr-only"> {{ item.name }} stats</span></a
              >
            </div>
          </div>
        </dd>
      </div>
    </dl>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ArrowDownIcon, ArrowUpIcon } from '@heroicons/vue/20/solid'
import { CursorArrowRaysIcon, EnvelopeOpenIcon, UsersIcon } from '@heroicons/vue/24/outline'
import axios from 'axios';

const HOST = "http://localhost:8080";

const stats = ref([
  { id: 1, name: '매출 건수', stat: '718', icon: UsersIcon, change: '122', changeType: 'increase' },
  { id: 2, name: '총 매출 금액(원)', stat: '\\12,345,678', icon: EnvelopeOpenIcon, change: '5.4%', changeType: 'increase' },
  { id: 3, name: '일 평균 매출(원)', stat: '\\123,456', icon: CursorArrowRaysIcon, change: '3.2%', changeType: 'decrease' },
]);
const formatStat = (value) => {
  return Number(value).toLocaleString();
};

onMounted(() => {
  axios.get(`${HOST}/api/sales/monthly`)
      .then(response => {
        if (response.status === 200) {
          stats.value[0].stat = formatStat(response.data.sales_count);
          stats.value[1].stat = formatStat(response.data.sales_amount);
          stats.value[2].stat = formatStat(parseInt(response.data.sales_amount / 30));
        }
      })
      .catch(error => {
        console.error('get category error: ', error);
      });
});
</script>
