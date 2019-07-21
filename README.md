# service_support_api  

macos 기준 실행 방법

1. mongodb 설치 터미널 오픈
> brew install mongodb
2. 터미널 오픈 아래 명령어 실행하여 mongod 실행
> nohup mongod & 
3. 소스 다운로드 터미널에서 아래 명령어 실행
> git clone git@github.com:YounghoChoi86/service_support_api.git 
4. Spring Boot 실행
> cd service_support_api
>  ./mvnw spring-boot:run 


1. 지원금 api 명세  
1) 파일 레코드에 해당하는 지원금 생성 api   
-요청 예제  
POST /supports/bulk HTTP/1.1  
Content-Length: 241  
Content-Type: application/json  
Host: localhost:8080  
  
{"year":2017,  
 "month":10,  
 "detail_amount":  
 {  
  "국민은행":2995,  
  "기타은행":2186,  
  "농협은행/수협은행":1436,  
  "신한은행":4518,  
  "외환은행":0,  
  "우리은행":4384,  
  "주택도시기금":8354,  
  "하나은행":1987,  
  "한국시티은행":0  
  }  
}  
  
-응답 예제   
HTTP/1.1 201 Created  
Content-Length: 787  
Content-Type: application/json;charset=UTF-8  
  
[{"id":"5d3445f433da6a3c84d57248","year":2017,"month":10,"bank":"bnk0133","amount":2995},  
{"id":"5d3445f433da6a3c84d57249","year":2017,"month":10,"bank":"bnk0140","amount":2186},  
{"id":"5d3445f433da6a3c84d5724a","year":2017,"month":10,"bank":"bnk0138","amount":1436},  
{"id":"5d3445f433da6a3c84d5724b","year":2017,"month":10,"bank":"bnk0135","amount":4518},  
{"id":"5d3445f433da6a3c84d5724c","year":2017,"month":10,"bank":"bnk0139","amount":0},  
{"id":"5d3445f433da6a3c84d5724d","year":2017,"month":10,"bank":"bnk0134","amount":4384},  
{"id":"5d3445f433da6a3c84d5724e","year":2017,"month":10,"bank":"bnk0132","amount":8354},  
{"id":"5d3445f433da6a3c84d5724f","year":2017,"month":10,"bank":"bnk0137","amount":1987},  
{"id":"5d3445f433da6a3c84d57250","year":2017,"month":10,"bank":"bnk0136","amount":0}]  
  
2) 최대 지원금 제공 기관 api  
-요청 예제  
GET /supports/topAmountBankOfYear HTTP/1.1  
Host: localhost:8080  
  
-응답 예제  
HTTP/1.1 200 OK  
Content-Length: 41  
Content-Type: application/json;charset=UTF-8  
  
{"year":2014,"bank":"주택도시기금"}  
  
3) 연도별 각 금융기관별 지원금 합계 api  
-요청 예제  
GET /supports/totalsOfYears HTTP/1.1  
Host: localhost:8080  
  
-응답 예제  
HTTP/1.1 200 OK  
Content-Type: application/json;charset=UTF-8  
Content-Length: 3514  
  
{"name":"주택기금정보",  
 "supports_of_years":  
 [  
 {"year":"2005년","total_amount":48016,"detail_amount":{"하나은행":3122,"농협은행/수협은행":1486,"우리은행":2303,"국민은행":13231,"신한은행":1815,"주택도시기금":22247,"외환은행":1732,"한국시티은행":704,"기타은행":1376}},  
 {"year":"2006년","total_amount":41210,"detail_amount":{"하나은행":3443,"농협은행/수협은행":2299,"우리은행":4134,"국민은행":5811,"신한은행":1198,"주택도시기금":20789,"외환은행":2187,"한국시티은행":288,"기타은행":1061}},  
 {"year":"2007년","total_amount":50893,"detail_amount":{"하나은행":2279,"농협은행/수협은행":3515,"우리은행":3545,"국민은행":8260,"신한은행":2497,"주택도시기금":27745,"외환은행":2059,"한국시티은행":139,"기타은행":854}},  
 {"year":"2008년","total_amount":67603,"detail_amount":{"하나은행":1706,"농협은행/수협은행":9630,"우리은행":4290,"국민은행":12786,"신한은행":1701,"주택도시기금":35721,"외환은행":941,"한국시티은행":69,"기타은행":759}},  
 {"year":"2009년","total_amount":96545,"detail_amount":{"하나은행":1226,"농협은행/수협은행":8775,"우리은행":13105,"국민은행":8682,"신한은행":3023,"주택도시기금":44735,"외환은행":6908,"한국시티은행":40,"기타은행":10051}},  
 {"year":"2010년","total_amount":114903,"detail_amount":{"하나은행":1872,"농협은행/수협은행":10984,"우리은행":15846,"국민은행":16017,"신한은행":2724,"주택도시기금":50554,"외환은행":11158,"한국시티은행":22,"기타은행":5726}},  
 {"year":"2011년","total_amount":206693,"detail_amount":{"하나은행":9283,"농협은행/수협은행":19847,"우리은행":29572,"국민은행":29118,"신한은행":11106,"주택도시기금":69236,"외환은행":8192,"한국시티은행":13,"기타은행":30326}},  
 {"year":"2012년","total_amount":275591,"detail_amount":{"하나은행":12534,"농협은행/수협은행":27253,"우리은행":38278,"국민은행":37597,"신한은행":21742,"주택도시기금":84227,"외환은행":19975,"한국시티은행":4,"기타은행":33981}},  
 {"year":"2013년","total_amount":265805,"detail_amount":{"하나은행":15167,"농협은행/수협은행":17908,"우리은행":37661,"국민은행":33063,"신한은행":21330,"주택도시기금":89823,"외환은행":10619,"한국시티은행":50,"기타은행":40184}},  
 {"year":"2014년","total_amount":318771,"detail_amount":{"하나은행":20714,"농협은행/수협은행":20861,"우리은행":52085,"국민은행":48338,"신한은행":28526,"주택도시기금":96184,"외환은행":11183,"한국시티은행":183,"기타은행":40697}},  
 {"year":"2015년","total_amount":374773,"detail_amount":{"하나은행":37263,"농협은행/수협은행":18541,"우리은행":67999,"국민은행":57749,"신한은행":39239,"주택도시기금":82478,"외환은행":20421,"한국시티은행":37,"기타은행":51046}},  
 {"year":"2016년","total_amount":400971,"detail_amount":{"하나은행":45485,"농협은행/수협은행":23913,"우리은행":45461,"국민은행":61380,"신한은행":36767,"주택도시기금":91017,"외환은행":5977,"한국시티은행":46,"기타은행":90925}},  
 {"year":"2017년","total_amount":295126,"detail_amount":{"하나은행":35629,"농협은행/수협은행":26969,"우리은행":38846,"국민은행":31480,"신한은행":40729,"주택도시기금":85409,"외환은행":0,"한국시티은행":7,"기타은행":36057}}  
 ]  
}  
  
4) 전체년도에서 특정은행의 지원금액 평균 중에 가장 큰 금액과 작은 금액을 출력하는 API  
-요청 : instituteName = 기관명으로 URLEncoding 형식  
GET /supports/amountMinMax?{instituteName} HTTP/1.1  
Host: localhost:8080  
  
-요청 예제 - 외환은행  
GET /supports/amountMinMax?bank=%EC%99%B8%ED%99%98%EC%9D%80%ED%96%89 HTTP/1.1  
Host: localhost:8080  
-응답 예제   
HTTP/1.1 200 OK  
Content-Length: 95  
Content-Type: application/json;charset=UTF-8  
  
{"bank":"외환은행","support_amount":[{"year":2017,"amount":0},{"year":2015,"amount":1702}]}  
  
  
2. 기관 관련 api  
1) 기관 등록 api   
  
-요청 방법 및 예제  
POST /institutes HTTP/1.1  
Accept: application/json  
Content-Type: application/json  
Host: localhost:8080  
Content-Length: 35  
  
{"instituteName":"테스트은행"}  
  
-응답 예제(생성 성공)  
HTTP/1.1 201 Created  
Content-Length: 61  
Content-Type: application/json;charset=UTF-8  
  
{"instituteCode":"bnk0151","instituteName":"테스트은행"}  
  
-요청 예제  
POST /institutes HTTP/1.1  
Accept: application/json  
Content-Length: 32  
Content-Type: application/json  
Host: localhost:8080  
  
{"instituteName":"외환은행"}  
-응답 예제(실패 - 중복된 기관 이름)  
HTTP/1.1 400 Bad Request  
Content-Type: application/json;charset=UTF-8  
Content-Length: 80  
  
{"code":400,"message":"InstituteName : 외환은행는 이미 존재합니다."}  
  
2) 특정 기관 가져오기 api  
-요청 방법  
GET /institutes/{insituteCode} HTTP/1.1  
Accept: application/json  
Host: localhost:8080  
  
-요청 예제  
GET /institutes/bnk0132 HTTP/1.1  
Accept: application/json  
Host: localhost:8080  
  
- 응답 예제  
HTTP/1.1 200 OK  
Content-Type: application/json;charset=UTF-8  
Content-Length: 89  
{  
    "instituteCode": "bnk0132",  
    "instituteName": "주택도시기금"  
}  
  
-요청 예제  
GET /institutes/TEST444444000 HTTP/1.1  
Accept: application/json  
Host: localhost:8080  
  
-응답 예제 (실패 - 기관코드가 존재하지 않음)  
HTTP/1.1 404 Not Found  
Content-Type: application/json;charset=UTF-8  
Content-Length: 89  
  
{"code":404,"message":"TEST444444000에 해당하는 기관을 찾을 수 없습니다."}  
  
3) 기관 업데이트 api   
-요청 방법   
PUT /institutes/{instituteCode} HTTP/1.1  
Accept: application/json  
Content-Length: 62  
Content-Type: application/json  
Host: localhost:8080  
-요청 예제  
PUT /institutes/bnk0151 HTTP/1.1  
Accept: application/json  
Content-Length: 62  
Content-Type: application/json  
Host: localhost:8080  
  
{"instituteCode":"bnk0151","instituteName":"테스트은행2"}  
  
HTTP/1.1 200 OK  
Content-Length: 62  
Content-Type: application/json;charset=UTF-8  
  
{"instituteCode":"bnk0151","instituteName":"테스트은행2"}  
  
4) 기관 삭제 api   
-요청 방법   
DELETE /institutes/{instituteCode} HTTP/1.1  
Accept: application/json  
Host: localhost:8080  
  
-요청 예제   
DELETE /institutes/bnk0152 HTTP/1.1  
Accept: application/json  
Host: localhost:8080  
  
-응답 예제  
HTTP/1.1 200 OK  
Content-Length: 62  
Content-Type: application/json;charset=UTF-8  
  
{"instituteCode":"bnk0152","instituteName":"테스트은행2"}  
  
5) 기관 목록 가져오기 api   
GET /institutes HTTP/1.1  
Accept: application/json  
Host: localhost:8080  
  
HTTP/1.1 200 OK  
Content-Length: 557  
Content-Type: application/json;charset=UTF-8  
  
[  
{"instituteCode":"bnk0132","instituteName":"주택도시기금"},  
{"instituteCode":"bnk0133","instituteName":"국민은행"},  
{"instituteCode":"bnk0134","instituteName":"우리은행"},  
{"instituteCode":"bnk0135","instituteName":"신한은행"},  
{"instituteCode":"bnk0136","instituteName":"한국시티은행"},  
{"instituteCode":"bnk0137","instituteName":"하나은행"},  
{"instituteCode":"bnk0138","instituteName":"농협은행/수협은행"},  
{"instituteCode":"bnk0139","instituteName":"외환은행"},  
{"instituteCode":"bnk0140","instituteName":"기타은행"}  
]  
