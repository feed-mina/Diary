<template>
    <div>
        <h1>Universal-Cookie CRUD</h1>
        <button @click="createCookie">Create Cookie</button>
        <button @click="readCookie">Read Cookie</button>
        <button @click="updateCookie">Update Cookie</button>
        <button @click="deleteCookie">Delete Cookie</button>
    </div>
</template>

<script>
import Cookies from 'universal-cookie';

export default {
    setup() {
        const cookies = new Cookies();

        const createCookie = () => {
            cookies.set('username', 'JohnDoe', { path: '/', maxAge: 86400 }); // 1일 유지
            console.log('Cookie Created: username=JohnDoe');
        };

        const readCookie = () => {
            const value = cookies.get('username');
            console.log('Cookie Read: username=', value);
        };

        const updateCookie = () => {
            cookies.set('username', 'JaneDoe', { path: '/', maxAge: 86400 });
            console.log('Cookie Updated: username=JaneDoe');
        };

        const deleteCookie = () => {
            cookies.remove('username', { path: '/' });
            console.log('Cookie Deleted: username');
        };

        return {
            createCookie,
            readCookie,
            updateCookie,
            deleteCookie
        };
    }
};
</script>
