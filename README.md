# DolphinMovie_Server

- 당면 과제
 sslHandler 구현은 했으나 인증서의 문제로 safari나 rested 에선 접근되나 ios에서는 안됨 해결하자

__Netty__ 프레임워크 기반의 Dolphin_Movie_Client 용 REST API 서버 

---

## REST API

- 일간, 주간 박스오피스 정보 가져오기

  메소드: GET

  URI: http://domain:port/rank
  
  Response Body :
  
    { "boxofficeResult" : {
 
      "dailyMovies": [
      
        "title" : (string),
        
        "rankInten" : (int),
        
        "rankOldAndNew" : (boolean),
        
        "thumbnailLink" : (string),
        
        "link" : (string)
        
       ],
       
      "weeklyMovies": [
      
        "title" : (string),
        
        "rankInten" : (int),
        
        "rankOldAndNew" : (boolean),
        
        "thumbnailLink" : (string),
        
        "link" : (string)
        
       ]
       
    }
    
  }                   


- 회원 정보 
  - 로그인
  
    메소드: POST
  
    URI: http://domain:port/login
    
    Response Body :
  
    { 
    
      "user_info" : {
      
       "name" : (string),
       
       "id" : (string)
       
       },
       
       "result_code" : (int),
       
       "result" : (string),
       
       "error_msg" : (string)
       
     }
     

  
  - 로그아웃 
  
    메소드: POST
  
    URI: http://domain:port/logout
    
    Response Body:
    
    {
      
      "result_code" : (int),
      
      "result" : (string),
      
      "error_msg" : (string)
      
    }
  
  - ID 중복 확인 
  
    메소드: POST
  
    URI: http://domain:port/join/idconfirm
    
    Response Body:
    
    {
      
      "result_code" : (int),
      
      "result" : (string)
      
    }

  - 회원가입
  
    메소드: POST
  
    URI: http://domain:port/join
    
    Response Body :
  
    { 
    
      "user_info" : {
      
       "name" : (string),
       
       "id" : (string),
       
       "password" : (string)
       
       },
       
       "result_code" : (int),
       
       "result" : (string),
       
       "error_msg" : (string)
       
     }

- 국내 영화관 정보 가져오기

  메소드: GET

  URI: http://domain:port/theater

  Response Body :
  
  {
  
    "theaterResult" : {
    
      "theaters" : [
        
        "name" : (string),
        
        "address" : (string),
        
        "lot_number" : (string),
        
        "xpos" : (float),
        
        "ypos" : (float),
        
        "open" : (boolean),
        
        "link" : (string)
        
      ]
      
    }
    
   }

