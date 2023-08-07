package blueeye.wx;

/**
 * @Author SDJin
 * @CreationDate 2023/6/26 11:42
 * @Description ：
 */
public class AccessToken {
    private String accessToken;
    //过期时间 当前系统时间+微信传来的过期时间
    private Long expiresTime;

    public AccessToken(String accessToken, String expiresIn) {
        this.accessToken = accessToken;
        this.expiresTime = System.currentTimeMillis() + Integer.parseInt(expiresIn) * 1000;
    }

    /**
     * 判断token是否过期
     *
     * @return
     */
    public boolean isExpired() {
        return System.currentTimeMillis() > expiresTime;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Long getExpiresTime() {
        return expiresTime;
    }

    public void setExpiresTime(Long expiresTime) {
        this.expiresTime = expiresTime;
    }

    public AccessToken(String accessToken, Long expiresTime) {
        this.accessToken = accessToken;
        this.expiresTime = expiresTime;
    }

    public AccessToken() {
    }

}

