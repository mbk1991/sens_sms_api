# 네이버 클라우드 Sens SMS API
:네이버 클라우드 Sens SMS API의 Wrapper API. SMS 발송 기능이 필요한 솔루션의 경우 해당 모듈을 재사용하고자 개발.

# Specification
<ul>
  <li>java17</li>
  <li>Springboot 3.2.4</li>
  <li>json-simple 1.1.1</li>
</ul>

# Description
## SMS 전송 API
###### ㄴ POST http://{ServerIP}:33333/sms
<ul>
  <li>HTTP Method : POST</li>
  <li>Content-Type : application/json</li>
  <li>Encoding : charset=utf-8</li>
</ul>

<table>
  <th>구분</th>
  <th>인자</th>
  <th>타입</th>
  <th>설명</th>
  <th>필수여부</th>
  <th>샘플</th>
  <tr>
    <td>Request</td>
    <td>phoneNumbers</td>
    <td>String</td>
    <td>전화번호목록</td>
    <td>필수</td>
    <td>"010-0000-0001;010-0000-0002;010-0000-0003"</td>
  </tr>
  <tr>
    <td>Request</td>
    <td>message</td>
    <td>String</td>
    <td>메시지본문</td>
    <td>필수</td>
    <td>"test Message!!!"</td>
  </tr>
  <tr>
    <td>Response</td>
    <td>-</td>
    <td>String</td>
    <td>응답문자열</td>
    <td>-</td>
    <td>"success"</td>
  </tr>
</table>

## Curl Test
curl -X POST -H "Content-Type:application/json; charset=utf-8"\ <br> 
-d '{"phoneNumbers":"010-0000-0001;010-0000-0002;010-0000-0003","message":"test Message!!!"}'\ <br>
http://{ipaddress}:33333/sms <br>
