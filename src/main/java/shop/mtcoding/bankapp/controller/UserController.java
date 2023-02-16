package shop.mtcoding.bankapp.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import shop.mtcoding.bankapp.dto.user.JoinReqDto;
import shop.mtcoding.bankapp.dto.user.loginReqDto;
import shop.mtcoding.bankapp.handler.ex.CustomException;
import shop.mtcoding.bankapp.model.user.User;
import shop.mtcoding.bankapp.model.user.UserRepository;
import shop.mtcoding.bankapp.service.UserService;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private HttpSession session;

    // SELECT 요청이지만 로그인만 post로 한다. (예외임!!)
    @PostMapping("/login")
    public String login(loginReqDto loginReqDto) {
        if (loginReqDto.getUsername() == null || loginReqDto.getUsername().isEmpty()) {
            throw new CustomException("username을 입력해주세요", HttpStatus.BAD_REQUEST);
        }
        if (loginReqDto.getPassword() == null || loginReqDto.getPassword().isEmpty()) {
            throw new CustomException("password를 입력해주세요", HttpStatus.BAD_REQUEST);
        }

        // 레파지토리 호출 (조회)
        User principal = userRepository.findByUsernameAndPassword(loginReqDto);

        if (principal == null) {
            throw new CustomException("아이디 혹은 비번이 틀렸습니다", HttpStatus.BAD_REQUEST);
        }

        // ------ HTTP 최초 로직!!!!!!!!
        // HTTP로 만들어진 서버는 stateless 이다.
        // stateless 상태를 저장하지 않는 서버!!
        // 클라이언트가 request(get,post,put,delete) 요청을 하면!!
        // 서버는 거기에 해당하는 처리를 하고, response 응답을 한다.
        // 서버는 클라이언트의 정보를 저장하지 않는다.
        // 서버는 멀티쓰레드 프로세스이다. 서버는 다수의 클라이언트의 요청 동시에 받을 수 있다.
        // 부하를 방지하기 위해서 클라이언트 정보를 기억안함.

        // 클라이언트의 정보를 기억해야하는 stateful 한 서버가 필요해진다.
        // 그 저장공간에 session이다.
        // 그래서 세션에 저장해야한다.

        // 아파치/톰켓의 저장 영역
        // (request - 응답이 되는 순간 사라짐, session - 브라우저가 켜져 있는 동안)
        session.setAttribute("principal", principal);

        return "redirect:/";
    }

    @PostMapping("/join")
    public String join(JoinReqDto joinReqDto) { // Dto로 받는것이 좋다 한방에 object안에 넣어준다.
        // 1.Post,Put일 때만 유효성 검사(이것보다 우선되는 것이 인증 검사이다.)
        if (joinReqDto.getUsername() == null ||
                joinReqDto.getUsername().isEmpty()) {
            throw new CustomException("username을 입력해주세요", HttpStatus.BAD_REQUEST);
        }
        if (joinReqDto.getPassword() == null ||
                joinReqDto.getPassword().isEmpty()) {
            throw new CustomException("password을 입력해주세요", HttpStatus.BAD_REQUEST);
        }
        if (joinReqDto.getFullname() == null ||
                joinReqDto.getFullname().isEmpty()) {
            throw new CustomException("fullname을 입력해주세요", HttpStatus.BAD_REQUEST);
        }

        // 컨벤션 : post(insert),put(update),delete 할때만 하기
        // 서비스 호출 => 회원가입();

        userService.회원가입(joinReqDto);

        return "redirect:/loginForm";
    }

    @GetMapping("/loginForm")
    public String loginForm() {
        return "user/loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm() {
        return "user/joinForm";
    }
}
