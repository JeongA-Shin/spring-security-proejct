package group.springsecurityproject.config.oauth.provider;

/**
 * 각 사이트마다 반환해주는 사용자 정보의 키 값이 다름. 따라서 그냥 PrincipalOauth2UserService에서 구현하기엔 키 이름만 달라지고 반복되는 게 너무 많아짐.
 * 그래서 좀 더 보기 쉽게 하기 위해,
 * 각 사이트마다 꼭 받아와야하는 정보들에 대한 getter들은 공통화되어 있기 때문에, 그리고 꼭 구현해야 하기 때문에 인터페이스로 만듦
 * 
 * 그리고 이렇게 해주면 각 사이트별 반환되는 user정보를 담는 객체의 자료형을 (PrincipalOauth2UserService에서) 일일히 선언해줄 필요 없이 그냥 OAuth2UserInfo로만 선언해서 담아줄 수 있음
 */

public interface OAuth2UserInfo { //내가 필요해서 만든 인터페이스임
    String getProviderId();
    String getProvider();
    String getEmail();
    String getName();
}
