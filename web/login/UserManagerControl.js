function UserMenuControl(userLevel){
    if(userLevel==0){
        initSystemManagerMenu();
    }else if(userLevel==1){
        initCommonManagerMenu();
    }else if(userLevel==2){

    }
}

// 初始化普通管理员菜单
function initCommonManagerMenu(){
    let ele=document.createElement('li');
    ele.innerHTML="<li><cite></cite><a onClick='getUserRecordTable()'>查看用户信息</a><i></i></li>";
    document.getElementById("ManagerUser").appendChild(ele);
    let other=document.createElement('li');
    other.innerHTML="<li><cite></cite><a onClick='CheckUserApply()'>审核注册信息</a><i></i></li>";
    document.getElementById("ManagerUser").appendChild(other);
}

// 初始化管理员用户菜单
function initSystemManagerMenu(){
    let ele=document.createElement('li');
    ele.innerHTML="<li><cite></cite><a onClick='getUserRecordTable()'>查看用户信息</a><i></i></li>";
    document.getElementById("ManagerUser").appendChild(ele);
    let other=document.createElement('li');
    other.innerHTML="<li><cite></cite><a onClick='CheckUserApply()'>审核注册信息</a><i></i></li>";
    document.getElementById("ManagerUser").appendChild(other);
}

// 审核注册信息 点击函数
function CheckUserApply(){
    window.parent.frames['rightFrame'].location="login/checkDataTable.html?_="+Math.random();
}

// 实现页面跳转
// 查看用户信息
function getUserRecordTable(){
    window.parent.frames['rightFrame'].location="login/DataTable.html?_="+Math.random();
}
// 删除用户信息
function deleteUserRecord(){
    // 调用[login/DataTable.html]下的函数
    alert("请在表格最后一栏中直接删除！");
    window.parent.frames['rightFrame'].location="login/DataTable.html?_="+Math.random();
}
// 修改用户信息
function modifyUserRecord(){
    //
    alert("请在表格操作栏进行修改！")
    window.parent.frames['rightFrame'].location="login/DataTable.html?_="+Math.random();
}
