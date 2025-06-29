<template>
  <div id="echarts-dom" style="width: 500px;height: 500px"></div>
</template>

<script>
import * as echarts from 'echarts';
import request from "@/utils/request";

require("echarts/theme/macarons");

// 卡片风格渐变色组
const colorList = [
  {
    type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
    colorStops: [
      { offset: 0, color: '#4e8cff' },
      { offset: 1, color: '#c9e7ff' }
    ]
  },
  {
    type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
    colorStops: [
      { offset: 0, color: '#67C23A' },
      { offset: 1, color: '#e6fff7' }
    ]
  },
  {
    type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
    colorStops: [
      { offset: 0, color: '#E6A23C' },
      { offset: 1, color: '#fff5e6' }
    ]
  },
  {
    type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
    colorStops: [
      { offset: 0, color: '#F56C6C' },
      { offset: 1, color: '#ffe0e0' }
    ]
  },
  {
    type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
    colorStops: [
      { offset: 0, color: '#9C27B0' },
      { offset: 1, color: '#f3e6ff' }
    ]
  },
  {
    type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
    colorStops: [
      { offset: 0, color: '#00bcd4' },
      { offset: 1, color: '#e0f7fa' }
    ]
  }
];

export default {
  name: "home_echarts",
  data() {
    return {
      option: {
        barWidth: 44,
        tooltip: {
          trigger: 'axis',
          backgroundColor: '#fff',
          borderColor: '#e3eaf5',
          borderWidth: 1,
          padding: 16,
          textStyle: { color: '#1a237e', fontWeight: 700, fontSize: 16 },
          extraCssText: 'box-shadow:0 8px 32px rgba(80,120,200,0.13);border-radius:16px;',
          formatter: params => {
            const p = params[0];
            const color = p.color;
            return `<div style='display:flex;align-items:center;gap:8px;'><span style='display:inline-block;width:14px;height:14px;border-radius:4px;background:${color};margin-right:6px;'></span><span style='font-size:18px;font-weight:800;'>${p.name}</span></div><div style='color:${color};font-size:16px;'>住宿人数：<b>${Math.round(p.value)}</b> 人</div>`;
          }
        },
        xAxis: {
          data: [],
          axisLine: { lineStyle: { color: '#e3eaf5', width: 2 } },
          axisLabel: { color: '#3a4a5d', fontWeight: 700, fontSize: 17, margin: 16 },
          axisTick: { show: false },
        },
        yAxis: {
          type: "value",
          minInterval: 1,
          axisLine: { show: false },
          axisLabel: {
            color: '#8fa2b7',
            fontWeight: 600,
            fontSize: 15,
            formatter: function (value) { return Math.round(value); }
          },
          splitLine: { lineStyle: { color: '#f2f4f8', type: 'dashed', width: 2 } }
        },
        series: [
          {
            name: '人数',
            type: 'bar',
            data: [],
            itemStyle: {
              borderRadius: [18, 18, 8, 8],
              color: function(params) {
                return colorList[params.dataIndex % colorList.length];
              },
              shadowColor: 'rgba(80,120,200,0.18)',
              shadowBlur: 32,
              shadowOffsetY: 8,
            },
            emphasis: {
              itemStyle: {
                borderRadius: [22, 22, 10, 10],
                shadowColor: 'rgba(80,120,200,0.28)',
                shadowBlur: 48,
                shadowOffsetY: 12,
              }
            },
            label: {
              show: true,
              position: 'top',
              color: '#fff',
              fontWeight: 900,
              fontSize: 20,
              backgroundColor: 'rgba(58,74,93,0.85)',
              borderRadius: 16,
              padding: [8, 18],
              shadowColor: 'rgba(80,120,200,0.18)',
              shadowBlur: 12,
              shadowOffsetY: 4,
              formatter: function(params) {
                return Math.round(params.value);
              }
            },
            animation: true,
            animationDuration: 900,
            animationEasing: 'cubicOut',
            animationDelay: function (idx) { return idx * 80; }
          },
        ],
        grid: {
          left: 60,
          right: 60,
          top: 60,
          bottom: 40,
          containLabel: true
        },
        backgroundColor: '#fff'
      },
      myEcharts: '',
      chartWidth: '',
      chartHeight: '',
    };
  },
  created() {
    this.getBuildingNum()
  },
  mounted() {
    this.createEcharts()
  },
  watch: {
    //观察option的变化
    option: {
      handler(newVal, oldVal) {
        if (this.myEcharts) {
          if (newVal) {
            this.myEcharts.setOption(newVal);
          } else {
            this.myEcharts.setOption(oldVal);
          }
        } else {
          this.createEcharts();
        }
      },
      deep: true //对象内部属性的监听，关键。
    }
  },
  methods: {
    createEcharts() {
      const chartDmo = document.getElementById("echarts-dom");
      this.myEcharts = echarts.init(chartDmo, null);
      this.myEcharts.setOption(this.option, true);
    },
    getBuildingNum() {
      //xAxis.data
      request.get("/building/getBuildingName").then(res => {
        if (res.code === '0') {
          this.option.xAxis.data = res.data
          //series.data
          request.get("/room/getEachBuildingStuNum/" + res.data.length).then(result => {
            if (result.code === '0') {
              // 保证人数为整数
              this.option.series[0].data = result.data.map(v => Math.round(v))
            }
          })
        }
      });
    },
  }
}
</script>

<style scoped>
#echarts-dom {
  width: 100% !important;
  max-width: 700px;
  height: 480px !important;
  margin: 0 auto;
  background: #fff;
  border-radius: 28px;
  box-shadow: 0 12px 48px 0 rgba(80,120,200,0.13), 0 2px 24px 0 rgba(80,120,200,0.10);
  padding: 32px 24px 24px 24px;
  transition: box-shadow 0.3s;
  border: 2.5px solid #e3eaf5;
  position: relative;
  overflow: visible;
}
</style>
