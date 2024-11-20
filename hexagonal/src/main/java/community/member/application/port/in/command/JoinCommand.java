package community.member.application.port.in.command;

import community.global.validation.SelfValidating;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class JoinCommand extends SelfValidating<JoinCommand> {

    /**
     * command 를 사용하는 이유
     * 요청하는 외부 시스템에 따라 객체의 형태가 달라질 수 있기 때문
     * ex) responseBody 로 넘어오던 id값이 pathvariable로 넘어올 경우, usecase 코드도 함께 수정해야 함
     * alidation Check를 Command에서만 하면 된다
     *
     * 값을 command 에서 검증할 수 있다. 검증에 대한 책임은 request dto가 아닌 command에서
     */
    @NotBlank(message = "login id는 비어 있을 수 없습니다.")
    @Size(min = 5, max = 12, message = "Login ID는 5자 이상 12자 이하여야 합니다.")
    private String loginId;

    @NotBlank(message = "password는 비어 있을 수 없습니다.")
    @Size(min = 5, max = 12, message = "Login ID는 5자 이상 12자 이하여야 합니다.")
    private String password;

    @Size(min = 2, max = 30, message = "Name은 2자 이상 30자 이하여야 합니다.")
    private final String name;

    public JoinCommand(String loginId, String password, String name) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.validateSelf();  // 생성자에서 검증 실행
    }
}
