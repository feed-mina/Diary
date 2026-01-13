import { render, screen, fireEvent } from '@testing-library/react';
import DynamicEngine from './DynamicEngine';

test('필수 입력값이 없으면 경고 메시지가 출력되는지 테스트', () => {
    const mockData = [{
        component_id: "user_email",
        label_text: "Email",
        component_type: "TEXT",
        is_required: true
    }];

    render(<DynamicEngine metadata={mockData} />);

    // 버튼 클릭 시뮬레이션
    const loginBtn = screen.getByText('로그인');
    fireEvent.click(loginBtn);

    // 경고 메시지가 콘솔이나 화면에 뜨는지 확인하는 로직
});