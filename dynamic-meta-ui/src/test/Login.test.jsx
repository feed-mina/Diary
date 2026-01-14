import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import axios from 'axios/dist/node/axios.cjs';
import Login from '../page/Login'; // 파일 경로 확인 필요

// axios를 가짜(mock)로 만듭니다.
jest.mock('axios');

test('서버에서 설계도를 가져와서 로그인 화면을 정상적으로 그리는지 테스트', async () => {
    // 1. 가짜 설계도 데이터 준비 (민아 님이 뽑아주신 소문자 구조)
    const mockMetadata = {
        status: "success",
        data: [
            { component_id: "user_email", label_text: "Email", component_type: "INPUT", sort_order: 1 },
            { component_id: "login_btn", label_text: "로그인", component_type: "BUTTON", action_type: "SUBMIT", sort_order: 2 }
        ]
    };

    // 2. axios.get이 위 데이터를 반환하도록 설정
    axios.get.mockResolvedValue({ data: mockMetadata });

    render(<Login />);

    // 3. 로딩 메시지가 떴다가 사라지고 "Email" 라벨이 나타나는지 기다립니다.
    await waitFor(() => {
        expect(screen.getByText(/Email/i)).toBeInTheDocument();
    });

    // 4. "로그인" 버튼도 잘 그려졌는지 확인
    expect(screen.getByText(/로그인/i)).toBeInTheDocument();
});

test('아이디 입력 후 로그인 버튼 클릭 시 서버로 데이터가 전송되는지 테스트', async () => {
    // 로그인 버튼 클릭 시 성공 응답을 보내주도록 가짜 설정
    axios.post.mockResolvedValue({ data: { message: "success" } });

    render(<Login />);

    // 화면이 그려질 때까지 대기
    const emailInput = await screen.findByLabelText(/Email/i);
    const loginBtn = screen.getByText(/로그인/i);

    // 사용자가 값을 입력함
    fireEvent.change(emailInput, { target: { value: 'mina@test.com' } });

    // 로그인 버튼 클릭
    fireEvent.click(loginBtn);

    // 5. axios.post가 올바른 주소와 데이터로 호출되었는지 검사
    expect(axios.post).toHaveBeenCalledWith("/api/auth/login", {
        user_email: 'mina@test.com'
    });
});