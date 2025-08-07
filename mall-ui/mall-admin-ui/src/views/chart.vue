<script setup>
    import * as echarts from 'echarts'
    import {onMounted} from "vue";
    import {ref} from "vue";
    import chartApi from "@/api/chart.js";

    const chartRef = ref()

    onMounted(() => {
        //echarts.init(document.getElementById('myChart'));
        const myChart = echarts.init(chartRef.value);
        
        // Loading state
        myChart.showLoading({
            text: 'Loading data...',
            color: '#1890ff',
            textColor: '#000',
            maskColor: 'rgba(255, 255, 255, 0.8)',
        });
        
        chartApi.selectClassCount().then((result) => {
            // Hide loading state
            myChart.hideLoading();
            
            if (result.code == 0) {
                const option = {
                    title: {
                        text: 'Product Categories',
                        subtext: 'Distribution by Category',
                        left: 'center'
                    },
                    tooltip: {
                        trigger: 'item'
                    },
                    legend: {
                        orient: 'vertical',
                        left: 'left'
                    },
                    series: [
                        {
                            name: 'Category',
                            type: 'pie',
                            radius: '50%',
                            data: result.data,
                            emphasis: {
                                itemStyle: {
                                    shadowBlur: 10,
                                    shadowOffsetX: 0,
                                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                                }
                            }
                        }
                    ]
                };

                // Use the specified configuration and data to display the chart.
                myChart.setOption(option);
            } else {
                // Handle error in result
                console.error('Error in API response:', result.msg);
                showErrorMessage(myChart, 'Failed to load category data');
            }
        }).catch(error => {
            // Hide loading state
            myChart.hideLoading();
            
            // Show error message in chart
            console.error('Error fetching category data:', error);
            showErrorMessage(myChart, 'Failed to load category data');
        });

        window.onresize = function () {
            myChart.resize();
        };
        
        // Function to display error message in chart
        function showErrorMessage(chart, message) {
            chart.setOption({
                title: {
                    text: 'Error',
                    subtext: message,
                    left: 'center',
                    textStyle: {
                        color: '#ff4d4f'
                    }
                },
                graphic: [
                    {
                        type: 'text',
                        left: 'center',
                        top: 'middle',
                        style: {
                            text: '⚠️ Data unavailable',
                            fontSize: 20,
                            fill: '#ff4d4f'
                        }
                    }
                ]
            });
        }



    })

</script>

<template>
    <div ref="chartRef" style="width: 100%;height: 100%"></div>
</template>

<style scoped>

</style>