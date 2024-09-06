import { cilArrowBottom, cilArrowTop } from '@coreui/icons';
import CIcon from '@coreui/icons-react';
import {
    CWidgetStatsA,
} from '@coreui/react';
import { CChartLine } from '@coreui/react-chartjs';
import { getStyle } from '@coreui/utils';
import moment from 'moment';
import { useEffect, useState } from 'react';
import { Col, Row } from 'react-bootstrap';
import AuthStats from '../service/AuthStats';



const StatsWidget = () => {
    const [todayEarnings, setTodayEarnings] = useState(0);
    const [earningsOfRange, setEarningsOfRange] = useState(0);
    const [sumOfProjects, setSumOfProjects] = useState(0);
    const [sumOfProjectsRange, setSumOfProjectsRange] = useState(0);
    const [percentageDiff, setPercentageDiff] = useState(0);

    useEffect(() => {
        AuthStats.getTodayEarnings().then(
            (res) => {
                setTodayEarnings(res.data)
            });
        AuthStats.earningsOfRange(5, moment().format("YYYY-MM-DD"), moment().format("YYYY-MM-DD")).then(
            (res) => {
                setEarningsOfRange(res.data)
            });
        AuthStats.getSumOfProjects().then(
            (res) => {
                setSumOfProjects(res.data)
            });
        AuthStats.sumOfProjectsRange(5).then(
            (res) => {
                setSumOfProjectsRange(res.data)
            });
        AuthStats.getPercentageDiff().then(
            (res) => {
                setPercentageDiff(res.data)
            });
    }, [])

    return (

        <Row>
            <Col xxl={6}>
                <CWidgetStatsA
                    color={percentageDiff > 0 ? 'success' : 'danger'}
                    value={
                        <>
                            {todayEarnings} zł{' '}
                            <span className="fs-6 fw-normal">
                                ({Math.round(percentageDiff * 100) / 100} %<CIcon icon={percentageDiff > 0 ? cilArrowTop : cilArrowBottom} /> )
                            </span>
                        </>
                    }
                    title="Sprzedaż"
                    chart={
                        <CChartLine
                            className="mt-3 mx-3"
                            style={{ height: '70px' }}
                            data={{
                                labels: Object.keys(earningsOfRange).map((x) => x),
                                datasets: [
                                    {
                                        label: 'Sprzedaż: ',
                                        backgroundColor: 'transparent',
                                        borderColor: 'rgba(255,255,255,.55)',
                                        pointBackgroundColor: getStyle('--cui-primary'),
                                        data: Object.values(earningsOfRange).map((x) => x),
                                    },
                                ],
                            }}
                            options={{
                                plugins: {
                                    legend: {
                                        display: false,
                                    },
                                },
                                maintainAspectRatio: false,
                                scales: {
                                    x: {
                                        border: {
                                            display: false,
                                        },
                                        grid: {
                                            display: false,
                                            drawBorder: false,
                                        },
                                        ticks: {
                                            display: false,
                                        },
                                    },
                                    y: {
                                        display: false,
                                        grid: {
                                            display: false,
                                        },
                                        ticks: {
                                            display: false,
                                        },
                                    },
                                },
                                elements: {
                                    line: {
                                        borderWidth: 7,
                                        tension: 0.4,
                                    },
                                    point: {
                                        radius: 4,
                                        hitRadius: 10,
                                        hoverRadius: 10,
                                    },
                                },
                            }}
                        />
                    }
                />
            </Col>

            <Col xxl={6}>
                <CWidgetStatsA
                    color="primary"
                    value={
                        <>
                            {sumOfProjects} szt{' '}

                        </>
                    }
                    title="Projekty"
                    chart={
                        <CChartLine

                            className="mt-3 mx-3"
                            style={{ height: '70px' }}
                            data={{
                                labels: Object.keys(sumOfProjectsRange).map((x) => x),
                                datasets: [
                                    {
                                        label: 'Ilość',
                                        backgroundColor: 'transparent',
                                        borderColor: 'rgba(255,255,255,.55)',
                                        pointBackgroundColor: getStyle('--cui-primary'),
                                        data: Object.values(sumOfProjectsRange).map((x) => x),
                                    },
                                ],
                            }}
                            options={{
                                plugins: {
                                    legend: {
                                        display: false,
                                    },
                                },
                                maintainAspectRatio: false,
                                scales: {
                                    x: {
                                        border: {
                                            display: false,
                                        },
                                        grid: {
                                            display: false,
                                            drawBorder: false,
                                        },
                                        ticks: {
                                            display: false,
                                        },
                                    },
                                    y: {
                                        display: false,
                                        grid: {
                                            display: false,
                                        },
                                        ticks: {
                                            display: false,
                                        },
                                    },
                                },
                                elements: {
                                    line: {
                                        borderWidth: 7,
                                        tension: 0.4,
                                    },
                                    point: {
                                        radius: 4,
                                        hitRadius: 10,
                                        hoverRadius: 10,
                                    },
                                },
                            }}
                        />
                    }
                />
            </Col>
        </Row>
    )
}

export default StatsWidget