<template>
    <div>
        <button @click="setCookie">Set Cookie</button>
        <button @click="getCookie">Get Cookie</button>
        <button @click="removeCookie">Remove Cookie</button>
    </div>
</template>

<script>
import { ref } from 'vue';
import Cookies from 'universal-cookie';

export default {
    setup() {
        const cookies = new Cookies();

        const setCookie = () => {
            cookies.set('username', 'JaneDoe', { path: '/', maxAge: 86400 });
            console.log('Cookie Set: username=JaneDoe');
        };

        const setUserDataCookie = () => {
            cookies.set('userData', 'res.data', { path: '/', maxAge: 86400 });
            console.log('Cookie Set: username=JaneDoe');
        };

        const readCookie = () => {
            const value = cookies.get('username');
            console.log('Cookie Read: username=', value);
        };

        const getCookie = () => {
            console.log('Cookie Value:', cookies.get('username')); // JaneDoe
        };

        const getUserDataCookie = () => {
            console.log('Cookie Value:', cookies.get('userData')); 
        };
        const removeCookie = () => {
            cookies.remove('username');
            console.log('Cookie Removed: username');
        };


        const updateCookie = () => {
            cookies.set('username', 'JaneDoe', { path: '/', maxAge: 86400 });
            console.log('Cookie Updated: username=JaneDoe');
        };
        return {
            setCookie,
            getCookie,
            readCookie,
            updateCookie,
            removeCookie,
        };
    },
};
</script>
