<script lang="ts">
import { ref, computed } from 'vue'
import axios from 'axios'
import qs from 'qs'

class GoodSummary {
  id = 0;
  name = ""
  price = 0
}

class GoodDetail {
  id = 0
  name = ""
  price = 0
  info = 0
}

class Order {
  id = 0
  goodid = 0
  userid = 0
}

let goods: any = ref<GoodSummary[]>([])
let orders = ref<Order[]>([])

axios.get<GoodSummary[]>("http://175.178.103.84:8080/good/listGood", {
  params: {
    page: 0,
    pagesize: 12
  }
}).then(function (res) {
  goods.value = res.data
})

</script>

<script setup lang="ts">
let auth = ref(localStorage.getItem("Auth"))
let usrname = ref(localStorage.getItem("usrname"))
let isediting = ref(false)

if (usrname.value === "Admin") {
  axios.post("http://175.178.103.84:8080/admin/listOrder",
    null,
    {
      headers: {
        "Auth": auth.value
      },
    }).then((res) => {
      orders.value = res.data
    })
}

async function login() {
  const name = prompt("用户名")
  if (name === null)
    return
  const pswd = prompt("密码")
  if (pswd === null)
    return
  const data = (await axios.post("http://175.178.103.84:8080/user/login", qs.stringify({
    name: name,
    pswd: pswd
  }))).data
  if (data === null || data === "") {
    alert("登陆失败，用户名和密码不匹配")
    return
  }
  localStorage.setItem("Auth", data.jwt)
  localStorage.setItem("usrname", data.profile.name)
  auth.value = data.jwt
  usrname.value = data.profile.name
}

async function adminLogin() {
  const pswd = prompt("密码")
  if (pswd === null)
    return
  const data = (await axios.post("http://175.178.103.84:8080/admin/login", qs.stringify({
    pswd: pswd
  }))).data
  if (data === null || data === "") {
    alert("登陆失败，用户名和密码不匹配")
    return
  }
  localStorage.setItem("Auth", data)
  localStorage.setItem("usrname", "Admin")
  auth.value = data
  usrname.value = "Admin"

  goods = (await axios.post<GoodDetail[]>("http://175.178.103.84:8080/admin/list", qs.stringify({
    page: 0,
    pagesize: 12
  }), {
    headers: {
      "Auth": auth.value
    },
  })).data

  orders = (await axios.post("http://175.178.103.84:8080/admin/listOrder",
    null,
    {
      headers: {
        "Auth": auth.value
      },
    })).data
}

function logout() {
  auth.value = null;
  localStorage.removeItem("Auth")
  usrname.value = null
  localStorage.removeItem("usrname")
}
async function regis() {
  const name = prompt("用户名")
  if (name === null)
    return
  const pswd = prompt("密码")
  if (pswd === null)
    return
  const data = (await axios.post("http://175.178.103.84:8080/user/regis", qs.stringify({
    name: name,
    pswd: pswd
  }))).data
  if (data === "ok")
    alert("注册成功")
  else
    alert("注册失败")
}

async function buy(id: number) {
  const data = (await axios.post("http://175.178.103.84:8080/order/buy", qs.stringify({
    goodid: id
  }), {
    headers: {
      "Auth": auth.value
    },
  })).data
  alert(data)
}

async function getFile(id: number) {
  const data = (await axios.post("http://175.178.103.84:8080/admin/getFile", qs.stringify({
    id: id
  }), {
    headers: {
      "Auth": auth.value
    },
  })).data
  alert(data)
}

async function remove(id: number) {
  const data = (await axios.post("http://175.178.103.84:8080/admin/delete", qs.stringify({
    id: id
  }), {
    headers: {
      "Auth": auth.value
    },
  })).data
  alert(data)
}

async function edit(id: number, name: string, price: number, info: string) {
  isediting.value = !isediting
  if (isediting.value === false) {
    const data = (await axios.post("http://175.178.103.84:8080/admin/update", qs.stringify({
      id: id,
      name: name,
      price: price,
      info: info
    }), {
      headers: {
        "Auth": auth.value
      },
    })).data
    alert(data)
  }

}

let islog = ref(false)

</script>

<template>
  <header>
    <text>ABC电子书商城</text>
    <div v-if="auth">
      欢迎，{{ usrname }}！
      <button @click="logout">登出</button>
    </div>
    <div v-else>
      <button @click="login">登录</button>
      <button @click="regis">注册</button>
      <button @click="adminLogin">管理员登录</button>
    </div>
    <br />
  </header>

  <main>
    <div v-if="usrname !== 'Admin'">
      <table>
        <thead>
          <tr>
            <th>名称</th>
            <th>价格</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          <template v-for="{ name, price, id } in goods">
            <tr>
              <td>{{ name }}</td>
              <td>{{ price }}</td>
              <button @click="buy(id)">购买</button>
            </tr>
          </template>
        </tbody>
      </table>
    </div>

    <div v-else>
      <form method="post" enctype="multipart/form-data" action="http://175.178.103.84:8080/admin/add">
        <p>名称：<input type="text" name="name"></p>
        <p>价格：<input type="number" name="price"></p>
        <p>描述：<input type="text" name="info"></p>
        <p>文件：<input type="file" name="file"></p>
        <p><input type="submit" value="提交"></p>
      </form>
      <br />
      <button @click="islog = !islog">{{ islog ? "查看所有图书" : "查看所有订单" }}</button>
      <br />
      <table>
        <div v-show="!islog">
          <thead>
            <tr>
              <th>名称</th>
              <th>价格</th>
              <th>描述</th>
              <th>文件</th>
              <th></th>
              <th></th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            <template v-for="{ name, price, id, info } in goods">
              <tr>
                <td>{{ name }}</td>
                <td>{{ price }}</td>
                <td>{{ info }}</td>
                <td><button @click="getFile(id)">查看文件</button></td>
                <td></td>
                <td><button @click="remove(id)">删除</button></td>
                <td><button @click="edit(id, name, price, info)">编辑</button></td>
              </tr>
            </template>
          </tbody>
        </div>
        <div v-show="islog">
          <thead>
            <tr>
              <th>订单ID</th>
              <th>商品ID</th>
              <th>购买者ID</th>
            </tr>
          </thead>
          <tbody>
            <template v-for="{ id, goodid, userid } in orders">
              <tr>
                <td>{{ id }}</td>
                <td>{{ goodid }}</td>
                <td>{{ userid }}</td>
              </tr>
            </template>
          </tbody>
        </div>
      </table>
    </div>

  </main>
  <footer>
    -----------------------------
  </footer>
</template>

<style scoped>
</style>
