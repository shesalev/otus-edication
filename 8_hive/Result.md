## Задание 1:
Вывести динамику количества поездок помесячно

Запрос:
```
select month,count(*) 
 from (
    select trunc(trip_start_timestamp,'MM') month
    from chicago_taxi_trips_parquet
) t 
group by month
order by 1 desc
limit 24;
```

![GitHub Logo](Task_1.png
)

## Задание 2:
Вывести топ-10 компаний (company) по выручке (trip_total)

Запрос:
```
select * from (
    select company,sum(trip_total) as amount
    from chicago_taxi_trips_parquet
    group by company
) t
order by amount desc
limit 10;

```

![GitHub Logo](Task_2.png
)

## Задание 3:
Подсчитать долю поездок <5, 5-15, 16-25, 26-100 миль
Запрос:
```
select trip_5 /cnt * 100 as ratio_trip_5
       ,trip_5_15/cnt * 100 as ration_trip_5_15
       ,trip_15_25/cnt * 100 as ration_trip_15_25
       ,trip_26_100/cnt * 100 as ration_trip_26_100
from (       
    select
           sum(case when trip_miles < 5 then 1 else 0 end) as trip_5
           ,sum(case when trip_miles >= 5 and trip_miles < 15 then 1 else 0 end) as trip_5_15
           ,sum(case when trip_miles >= 15 and trip_miles <= 25 then 1 else 0 end) as trip_15_25
           ,sum(case when trip_miles >= 26 and trip_miles <= 100 then 1 else 0 end) as trip_26_100
           ,count(*) cnt
    from chicago_taxi_trips_parquet
) t;
```

![GitHub Logo](Task_3.png
)