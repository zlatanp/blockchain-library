var x = document.cookie;
var parts = x.split("email@");
var second = parts[1];

var email;
var type;
if("undefined" !== typeof second){
    var pom = second.split("#");
    type = pom[0];
    email = pom[1];
}

var journals = [];

$(document).ready(function(){

   $('#searchDiv').hide();
   $('#areaCodeTable').hide();
   $('#allArticles').show();
   $('#addArticleButton').hide();
   $('#searchDivResult').hide();
   $('#reviewDivResult').hide();
   $('.profesorLeft').hide();
   getAllArticles();
    $( function() {
          $("#dialog").dialog({
               autoOpen: false,
               show: {
                   effect: "blind",
                   duration: 500
                   },
               hide: {
                   effect: "explode",
                   duration: 500
                   }
          });

      });

     $( function() {
          $("#dialog3").dialog({
               autoOpen: false,
               show: {
                   effect: "blind",
                   duration: 500
                   },
               hide: {
                   effect: "explode",
                   duration: 500
                   }
          });

      });

   if("undefined" !== typeof second){
       $('#usernamePlace').show();
       $('#usernamePlaceholder').html('<span class="glyphicon glyphicon-user"></span> ' + email);
       $('#login').hide();
       $('#logout').show();

       if(type === "student") {
           $('.adminLeft').hide();
           $('.profesorLeft').hide();
           $('#addArticleButton').show();
       }

       if(type === "professor") {
           $('.adminLeft').hide();
           $('.profesorLeft').hide();
           getNotifications();
       }

       if(type === "admin") {
           $('.adminLeft').show();
           $('.profesorLeft').show();

       }
   }else{
       $('#usernamePlace').hide();
       $('#logout').hide();
       $('#login').show();
       $('.adminLeft').hide();
   }
});


function logout(){
    document.cookie = "email" + '@; Max-Age=0';
    window.location.href="index.html";
}

function login(){
    window.location.href="home.html";
}

function searchItems(){
    $('#searchDiv').hide();
    $('#areaCodeTable').hide();
    $('#searchDivResult').hide();
    $("#dialog").dialog('close');
    $("#dialog2").dialog('close');
    $('#searchDiv').show();
    $('.adminLeft').hide();
    $('.profesorLeft').hide();
    $('#searchKeywords').tagsInput({'width':'220px' });
    $('#searchAuthors').tagsInput({'width':'220px' });
    $('#searchMembers').tagsInput({'width':'220px' });

    $("#searchAreaNameSelect").empty();

    $.ajax({
        type: 'GET',
        url: 'areacode/getAll',
        dataType: 'json',
        success: function(data){
            console.log(data);
            if(data.length > 0){
                $("#searchAreaNameSelect option[value='empty']").remove();
                for(var i =0; i<data.length; i++){
                    $('#searchAreaNameSelect').append('<option>' + data[i].name + '</option>');
                }
            }
        }
    });
}

function searchMyChecked(){
    var checkBox = document.getElementById("searchMyCheck");
    var text = document.getElementById("searchOperation");
    if(checkBox.checked == true){
      text.style.display = "block";
    }else{
       text.style.display = "none";
    }
}

function searchSomeArticles(){

    $('#allArticles').hide();
    $("#articleTableSearch tbody").empty();
    $('#searchDivResult').show();
    $('#reviewDivResult').hide();

    var searchTerms = [];

    var fieldCount = 0;

    var j = document.getElementsByName('searchArticleName');
    var searchArticleName = j[0].value;

    var searchAuthors = $("#searchAuthors").val();

    var searchMembers = $("#searchMembers").val();

    var searchKeywords = $("#searchKeywords").val();

    var l = document.getElementsByName('searchPdfContent');
    var searchPdfContent = l[0].value;

    var searchAreaName = $('#searchAreaNameSelect').find(":selected").text();

    var checkBox = document.getElementById("searchMyCheck");

    if(searchArticleName=="" && searchAuthors=="" && searchMembers=="" && searchKeywords=="" && searchPdfContent=="" && searchAreaName==""){
        toastr.error("You must enter at least one parameter for search!");
        return false;
    }

    if(searchArticleName!=""){
        fieldCount += 1;
        searchTerms.push(searchArticleName);
    }

    if(searchAuthors!=""){
       fieldCount += 1;
       var searchAuthorsWords = searchAuthors.split(",");
       for(var z =0; z<searchAuthorsWords.length; z++){
           searchTerms.push(searchAuthorsWords[z]);
       }
    }

    if(searchMembers!=""){
       fieldCount += 1;
       var searchMembersWords = searchMembers.split(",");
       for(var z =0; z<searchMembersWords.length; z++){
           searchTerms.push(searchMembersWords[z]);
       }
    }

    if(searchKeywords!=""){
        fieldCount += 1;
        var keywordWords = searchKeywords.split(",");
        for(var z =0; z<keywordWords.length; z++){
            searchTerms.push(keywordWords[z]);
        }
    }

    if(searchPdfContent!=""){
       fieldCount += 1;
       searchTerms.push(searchPdfContent);
    }

    if(searchAreaName != ""){
        fieldCount += 1;
        searchTerms.push(searchAreaName);
    }

    var searchOperation;

    if(checkBox.checked == true){
        searchOperation = $('#searchOperationSelect').find(":selected").text();
    }else{
        searchOperation = "OR";
    }

    $.ajax({
        type: 'POST',
        url: 'elastic/getArticleCombined',
        dataType: 'json',
        data: {articleName : searchArticleName, authors : searchAuthors, members : searchMembers, articleKeywords : searchKeywords, articleContent : searchPdfContent, areaName : searchAreaName, operationType : searchOperation, fieldCount : fieldCount},
        success: function(data){
            console.log(data);
            var resultSize = data.hits.total;
            if(resultSize == 0){
                $("#articleTableSearch").append("<tr><td colspan=\"6\" style=\"color:red\"><p><b>No Results!</b></p></td></tr>");
            }else{
                var resultList = data.hits.hits;
                console.log(resultList);
                for(var i =0; i<resultList.length; i++){
                    var titleResult = resultList[i]._source.title;
                    var authorsResult = resultList[i]._source.author;
                    var apstractResult = resultList[i]._source.apstract
                    var keywordsResult = resultList[i]._source.keywords;
                    var comiteeMembersResult = resultList[i]._source.commiteeMembers;
                    var signedByResult = resultList[i]._source.signedBy;

                    var filename = resultList[i]._source.filename;
                    var content = resultList[i]._source.content;
                    $('#articleTableSearch').append('<tr><td>' + titleResult + '</td><td>' + authorsResult.firstName + ' ' + authorsResult.lastName + '</td><td>' + apstractResult + '</td><td>' + keywordsResult + '</td><td>' + comiteeMembersResult[0].firstName + ' ' + comiteeMembersResult[0].lastName +', ' +  comiteeMembersResult[1].firstName + ' ' + comiteeMembersResult[1].lastName +', ' + comiteeMembersResult[2].firstName + ' ' + comiteeMembersResult[2].lastName + '</td><td id=' + filename + 'signedusers></td><td id=' + filename + '></td></tr>');

                    //Highlight
                    var context = document.querySelector('#articleTableSearch'); // requires an element with class "context" to exist
                    var instance = new Mark(context);
                    instance.mark(searchTerms, {  "element": "span",
                                                  "className": "highlightER"
                                               }
                                 );

                    if(signedByResult.length == 0){
                           $('#'+ filename + 'signedusers').append("Not yet Signed!");
                    }else{
                        for(var p =0; p<signedByResult.length; p++){
                            $('#'+ filename + 'signedusers').append(signedByResult[p].firstName + ' ' + signedByResult[p].lastName + '; ');
                        }
                    }

                    $.ajax({
                        type: 'GET',
                        url: 'article/getArticleById',
                        dataType: 'json',
                        async: false,
                        data: {id : filename},
                        success: function(data){
                            console.log(data);
                            $('#'+ filename + '').append('<td><a href="#" onclick="downloadAttachment(\''+ data.file.data +'\')">Download PDF</a></td>');
                        }
                    });
                }
            }
          }
    });

    return false;

}


function getAllArticles(){
    $.ajax({
       type: 'GET',
       url: 'article/getAll',
       dataType: 'json',
       success: function(data){
           console.log(data);
           $("#articleTable tbody").empty();
           if(data.length > 0){
               for(var i =0; i<data.length; i++){
                    var ide = data[i].id;
                    var signedByResult = data[i].signedBy;
                    $("#articleTable").append('<tr><td>' + data[i].title + '</td><td>' + data[i].author.firstName + ' ' + data[i].author.lastName + '</td><td>' + data[i].apstract + '</td><td>' + data[i].keywords + '</td><td>' + data[i].committeeMembers[0].firstName + ' ' + data[i].committeeMembers[0].lastName +', ' +  data[i].committeeMembers[1].firstName + ' ' + data[i].committeeMembers[1].lastName +', ' + data[i].committeeMembers[2].firstName + ' ' + data[i].committeeMembers[2].lastName + '</td><td id=' + ide + 'signedusers1></td></tr>');
                    if(signedByResult.length == 0){
                        $('#'+ ide + 'signedusers1').append("Not yet Signed!");
                    }else{
                        for(var p =0; p<signedByResult.length; p++){
                            $('#'+ ide + 'signedusers1').append(signedByResult[p].firstName + ' ' + signedByResult[p].lastName + '; ');
                       }
                    }
               }
           }else{
               $("#articleTable").append('<tr><td colspan=\"5\" style=\"color:red\"><b>No Articles!</b></td></tr>');
           }
       }
   });
}

function newAreaCode(){
    $('#areaCodeTable').hide();
    $("#dialog").dialog("open");
    $("#dialog2").dialog('close');
    $('#allArticles').hide();
    $('#articles').hide();
    $('#searchDiv').hide();
    $('#searchDivResult').hide();

}

function addNewAreaCode(){
    var i = document.getElementsByName('dCode');
    var codeValue = i[0].value;

    var j = document.getElementsByName('dName');
    var nameValue = j[0].value;

    if(codeValue == "" || nameValue == ""){
        toastr.error("Fields can not be empty!");
        return false;
    }else{
        $.ajax({
            type: 'POST',
            url: 'areacode/addCode',
            dataType: 'json',
            data: {code : codeValue, name : nameValue},
            success: function(data){
                console.log(data);
            },
            complete: function(data){
            var response = data.responseText;
                console.log(response);
                if(response == "nill")
                    toastr.error("Bad request!");
                if(response == "codeErr")
                    toastr.error("Category with that code already exist!");
                if(response == "ok"){
                    toastr.success("Category added!");
                    $("#dCode").val("");
                    $("#dName").val("");
                    $("#dialog").dialog('close');
                }
            }
        });
    }
}

function allAreaCodes(){
    $("#dialog").dialog('close');
    $("#dialog2").dialog('close');
    $('#areaCodeTable').show();
    $('#allArticles').hide();
    $('#articles').hide();
    $('#searchDiv').hide();
    $('#searchDivResult').hide();

    $.ajax({
        type: 'GET',
        url: 'areacode/getAll',
        dataType: 'json',
        success: function(data){
            console.log(data);
            $("#areaCodeTable tbody").empty();
            if(data.length > 0){
               for(var i =0; i<data.length; i++){
                   $("#areaCodeTable").append('<tr><td>' + data[i].code + '</td><td>' + data[i].name + '</td><td><input type="button" onclick="deleteArea(\''+ data[i].code +'\')" value="Delete Area"></td></tr>');
               }
            }else{
                $("#areaCodeTable").append('<tr><td>No Area Codes</td></tr>');
            }
        }
    });
}

function deleteArea(areaCode){
    $.ajax({
        type: 'POST',
        url: 'areacode/deleteCode',
        dataType: 'json',
        data: {code : areaCode},
        complete: function(data){
            toastr.success("Successfully removed Area with code " + areaCode + " !");
            allAreaCodes();
        }
    });
}

function addArticleButton(){
    $('#searchDiv').hide();
    $('#searchDivResult').hide();
    $('#allArticles').show();
    $("#dialog3").dialog('open');
    $("#dArticleAuthor").val(email);
    $('#dArticleKeywords').tagsInput({'width':'250px' });

    $("#DarticleCodeSelect option[value='empty']").remove();
    $.ajax({
        type: 'GET',
        url: 'areacode/getAll',
        dataType: 'json',
        success: function(data){
            console.log(data);
            $("#areaCodeTable tbody").empty();
            if(data.length > 0){
                for(var i =0; i<data.length; i++){
                    $("#DarticleCodeSelect").append('<option>' + data[i].name + '</option>');
                }
            }else{
                $("#DarticleCodeSelect").append('<option>No Area Code</option>');
            }
        }
    });

    $("#DfirstCommittee option[value='empty']").remove();
    $("#DsecondCommittee option[value='empty']").remove();
    $("#DthirdCommittee option[value='empty']").remove();
    $.ajax({
        type: 'GET',
        url: 'user/getAll',
        dataType: 'json',
        success: function(data){
            console.log(data);
            if(data.length > 0){
                for(var i =0; i<data.length; i++){
                    if(data[i].userType == "PROFESSOR"){
                        $("#DfirstCommittee").append('<option value=' + data[i].email + '>' + data[i].firstName + ' ' + data[i].lastName + '</option>');
                        $("#DsecondCommittee").append('<option value=' + data[i].email + '>' + data[i].firstName + ' ' + data[i].lastName + '</option>');
                        $("#DthirdCommittee").append('<option value=' + data[i].email + '>' + data[i].firstName + ' ' + data[i].lastName + '</option>');
                    }
                }
            }else{
                $("#DarticleCodeSelect").append('<option value="empty">No Professors</option>');
            }
        }
    });

}

function addNewArticleSubmit(){
        var ok = true;

        var i = document.getElementsByName('dArticleTitle');
        var dArticleTitle = i[0].value;

        var j = document.getElementsByName('dArticleKeywords');
        var dArticleKeywords = j[0].value;

        var k = document.getElementsByName('dArticleAbstract');
        var dArticleAbstract = k[0].value;

        var dArticleArea = $('#DarticleCodeSelect').find(":selected").text();

        var dfirstCommittee = $('#DfirstCommittee').find(":selected").text();

        var dsecondCommittee = $('#DsecondCommittee').find(":selected").text();

        var dthirdCommittee = $('#DthirdCommittee').find(":selected").text();


        if(dArticleTitle==""){
            toastr.error("You must enter Article Title!");
            return false;
        }

        if(dArticleKeywords==""){
            toastr.error("You must enter Article Keywords!");
            return false;
        }

        if(dArticleAbstract==""){
            toastr.error("You must enter Abstract!");
            return false;
        }

        if(dArticleArea==""){
            toastr.error("You must choose Article Area!");
            return false;
        }

        if(dfirstCommittee==""){
            toastr.error("You must choose First Committee!");
            return false;
        }

        if(dsecondCommittee==""){
            toastr.error("You must choose Second Committee!");
            return false;
        }

        if(dthirdCommittee==""){
            toastr.error("You must choose Third Committee!");
            return false;
        }

        if(dfirstCommittee == dsecondCommittee || dfirstCommittee == dthirdCommittee || dsecondCommittee == dthirdCommittee){
            toastr.error("You can not choose same committee member multiple times!");
            return false;
        }

        if( document.getElementById("articleFile").files.length == 0 ){
            toastr.error("You must choose File!");
            return false;
        }

        var filePDF = document.getElementById("articleFile");

        if(ok){
            alert("New Article has been successfully added!");
        }

        return ok;
}

function getNotifications(){
    $.ajax({
        type: 'GET',
        url: 'article/checkReview',
        dataType: 'json',
        data: {email : email},
        success: function(data){
                    console.log(data);
                    if(data.length > 0){
                        $('.profesorLeft').show();
                    }
                }
    });
}

function reviewArticles(){
    $('#searchDiv').hide();
    $('#areaCodeTable').hide();
    $('#allArticles').hide();
    $('#addArticleButton').hide();
    $('#searchDivResult').hide();
    $('#reviewDivResult').hide();
    $('.profesorLeft').hide();
    $('#reviewDivResult').show();

    $.ajax({
        type: 'GET',
        url: 'article/checkReview',
        dataType: 'json',
        data: {email : email},
        success: function(data){
                    console.log(data);
                    $("#reviewTable tbody").empty();
                    if(data.length > 0){
                        for(var i =0; i<data.length; i++){
                            var ide = data[i].id;
                            var signedByResult = data[i].signedBy;
                            $("#reviewTable").append('<tr><td>' + data[i].title + '</td><td>' + data[i].author.firstName + ' ' + data[i].author.lastName + '</td><td>' + data[i].apstract + '</td><td>' + data[i].keywords + '</td><td>' + data[i].committeeMembers[0].firstName + ' ' + data[i].committeeMembers[0].lastName +', ' +  data[i].committeeMembers[1].firstName + ' ' + data[i].committeeMembers[1].lastName +', ' + data[i].committeeMembers[2].firstName + ' ' + data[i].committeeMembers[2].lastName + '</td><td id=' + ide + 'signedusers2></td><td><a href="#" onclick="openAttachment(\''+ data[i].file.data +'\')">Download PDF</a></td><td><input type="checkbox" name="checkedboxs" value=' + ide + '></td></tr>');
                            if(signedByResult.length == 0){
                                $('#'+ ide + 'signedusers2').append("Not yet Signed!");
                            }else{
                                for(var p =0; p<signedByResult.length; p++){
                                    $('#'+ ide + 'signedusers2').append(signedByResult[p].firstName + ' ' + signedByResult[p].lastName + '; ');
                               }
                            }
                        }
                    }
                }
    });
}

function signSelectedArticles(){
    var checkedValue = [];
    $.each($("input[name='checkedboxs']:checked"), function(){
        checkedValue.push($(this).val());
    });
    $.ajax({
        type: 'POST',
        url: 'article/signArticles',
        dataType: 'json',
        data: {email : email, ids : checkedValue.join()},
        success: function(data){
            console.log(data);
        }
    });

    return false;
}

function openAttachment(data){
    var objbuilder = '';
    objbuilder += ('<object width="100%" height="100%"      data="data:application/pdf;base64,');
    objbuilder += (data);
    objbuilder += ('" type="application/pdf" class="internal">');
    objbuilder += ('<embed src="data:application/pdf;base64,');
    objbuilder += (data);
    objbuilder += ('" type="application/pdf" />');
    objbuilder += ('</object>');

    var win = window.open("","_blank","titlebar=yes");
    win.document.title = "My Title";
    win.document.write('<html><body>');
    win.document.write(objbuilder);
    win.document.write('</body></html>');
    layer = jQuery(win.document);
}

function downloadAttachment(data){
       let a = document.createElement("a");
       a.href = "data:application/octet-stream;base64,"+data;
       a.download = "documentName.pdf"
       a.click();
}