<template>
    <div>
        <button @click="login">Login</button>
        <button @click="logout">Logout</button>
    </div>
</template>

<script>
import Cookies from 'universal-cookie';

export default {
    setup() {
        const cookies = new Cookies();

        const login = () => {
            const token = 'sample_jwt_token';
            cookies.set('authToken', token, { path: '/', maxAge: 3600 }); // 1시간 유지
            console.log('User logged in, token set.');
        };

        const logout = () => {
            cookies.remove('authToken', { path: '/' });
            console.log('User logged out, token removed.');
        };

        return {
            login,
            logout
        };
    }
};
</script>
