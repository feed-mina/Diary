  <script>
import axios from 'axios';
export default {
    name: "EmailTemplate",
    data(){
        return{
            email:{
                to:"",
                subject:"",
                body:"",
            },
        };
    },
    methods:{
        async sendEmail(){
            try{
                const response = await axios.post('http://localhost:8080/api/auth/send',this.email);
                alert(response.data);
            }catch(error){
                console.error(error);
                alert("이메일 전송 실패");
            
            }
        },
    async semdHtmlEmail(){
        try{
            const response = await axios.post('http://localhost:8080/api/email/send-html', {
            to: 'receiver@example.com',
            subject: 'HTML Email',
            body: '<h1>Hello!</h1><p>This is an HTML email.</p>'
        });
        alert(response.data);

        }catch(error){
            alert('Failed to send email.');

        }
    },
    async sendEmailWithAttachment() {
    const formData = new FormData();
    formData.append('to', 'receiver@example.com');
    formData.append('subject', 'Attachment Email');
    formData.append('body', 'Here is an attachment.');
    formData.append('filePath', '/path/to/your/file.txt');

    try {
        const response = await axios.post('http://localhost:8080/api/email/send-attachment', formData);
        alert(response.data);
    } catch (error) {
        console.error(error);
        alert('Failed to send email.');
    }
}

    }
    
  };
  </script>


<template>
    <div>
<form class="emailTemplateForm" @submit.prevent="sendEmail">
   <div>
        <label for="to">ToL</label>
        <input type="email" v-model="email.to" required/>
    </div>
    <div>
        <label for="subject">Subject</label>
        <input type="text" v-model="email.subject" required/>
    </div>
    <div>
        <label for="body">Body</label>
        <textarea v-model="email.body" required></textarea>

    </div>
    <button type="submit">Send Email</button>
</form>
        <footer class="EmailTemplateFooter">
      <p>© 2024 AI 일기장. 모든 권리 보유.</p>
    </footer>
    </div>

    
  </template>
  
  <style scoped>
  .footer {
    margin-bottom: -100px;
    background-color: #222;
    color: white;
    text-align: center;
    /* padding: 10px 0; */
    /* margin-top: 20px; */
  }
  </style>
  