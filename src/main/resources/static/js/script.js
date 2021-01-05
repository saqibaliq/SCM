console.log("JS Working..");

const toggleSidebar = () =>{
if($('.sideBar').is(":visible")){
    $(".sideBar").css("display","none");
    $(".content").css("margin-left","0%");
}else{
    $(".sideBar").css("display","block");
    $(".content").css("margin-left","20%");
}
};

const search = () =>{
    let query = $("#search-input").val();
        
    if(query == ''){
        $(".search-result").hide();
    }else{
    console.log(query);
    //sending request to the server
    let url = `http://localhost:8080/search/${query}`;
    fetch(url).then((response) => {
            return response.json();
    }).then((data) => {
        console.log(data);
        let text = `<div class= 'list-group'>`
            data.forEach((contact) =>{
                    text+=`<a href='/user/${contact.cId}/contact' class="list-group-item list-group-item-action">${contact.cName}</a>`
            });
        text += '</div>'
        $(".search-result").html(text);
        $(".search-result").show();
    });

    }
};