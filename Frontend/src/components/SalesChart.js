import { getStyle } from '@coreui/utils';
import React, { useEffect, useState } from 'react';

import { CChartLine } from '@coreui/react-chartjs';
import AuthStats from '../service/AuthStats';

const SalesChart = (props) => {
    const [chartEarnings, setChartEarnings] = useState(0);
    

    useEffect(() => {
        AuthStats.earningsOfRange(props.days, props.startDate, props.endDate).then(
            (res) => {
                setChartEarnings(res.data)
            });
    })
    


    return (
        <CChartLine
            style={{ height: '300px', marginTop: '40px' }}
            data={{
                labels: Object.keys(chartEarnings).map((x) => x),
                datasets: [
                    {
                        label: 'Dzienna wartość sprzedaży',
                        backgroundColor: `rgba(${getStyle('--cui-info-rgb')}, .1)`,
                        borderColor: getStyle('--cui-info'),
                        pointHoverBackgroundColor: getStyle('--cui-info'),
                        borderWidth: 2,
                        data: Object.values(chartEarnings).map((x) => x),
                        fill: true,
                    },
                ],
            }}
            options={{
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false,
                    },
                },
                scales: {
                    x: {
                        grid: {
                            color: getStyle('--cui-border-color-translucent'),
                            drawOnChartArea: false,
                        },
                        ticks: {
                            color: getStyle('--cui-body-color'),
                        },
                    },
                    y: {
                        beginAtZero: true,
                        border: {
                            color: getStyle('--cui-border-color-translucent'),
                        },
                        grid: {
                            color: getStyle('--cui-border-color-translucent'),
                        },
                        ticks: {
                            color: getStyle('--cui-body-color'),
                            maxTicksLimit: 10,
                            stepSize: Math.ceil(250 / 5),
                        },
                    },
                },
                elements: {
                    line: {
                        tension: 0.4,
                    },
                    point: {
                        radius: 3,
                        hitRadius: 10,
                        hoverRadius: 4,
                        hoverBorderWidth: 3,
                    },
                },
            }}
        />

    )
}

export default SalesChart