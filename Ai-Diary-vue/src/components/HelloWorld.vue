import { mount } from '@vue/test-utils';
import HelloWorld from './HelloWorld.vue'; // 경로 수정

describe('HelloWorld', () => {
  it('renders correctly', () => {
    const wrapper = mount(HelloWorld);
    expect(wrapper.text()).toContain('Hello World');
  });
});
