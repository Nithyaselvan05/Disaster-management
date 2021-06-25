const express = require('express')
var Twit = require('twit')
var _=require('lodash')
const {Wit, log} = require('node-wit');
const client = new Wit({accessToken: 'F3FV2RYOWMGT5QWOEGU5JWI7TP2BMNHT'});
const app = express();
app.set('view engine', 'ejs');
let arr=[];
var mystr;

var T = new Twit({
  consumer_key:         'K9oyhuUoPv2EoEVFQ1GIkFwm2',
  consumer_secret:      '5C1R13kdtkcBh08hubVffCXo0pw9AebBptNuZGuBKhxFx0NiQu',
  access_token:         '854550746-zZUYhQUNIfw1TonqHgstaut8gPwaHgQPJeoa6ajz',
  access_token_secret:  'ud8qO948trhM8Fo4HAcI6EuladOfObxRhlZ0LFLxRz1SE',
  timeout_ms:           60*1000,  // optional HTTP request timeout to apply to all requests.
  strictSSL:            true,     // optional - requires SSL certificates to be valid.
})
app.get("/",function(req,res){
  T.get('search/tweets', { q: '#ChennaiFloods since:2015-7-11', count: 100}, function(err, result, response) {

    var final =result.statuses
    // console.log(final);
    // for(var i=0;i<final.length;i++){
    //   console.log(final[i].text);
    //   // var mystr=final[i].text;
    // }
        final.forEach(function(k){
          for(var o in k ){
            if(o==="text"){
               mystr=k[o]
               client.message(mystr, {})
               .then((data) => {

                 // arr.push(data);
                 // if(arr.length>10){
                 //   arr.pop();
                 // }
                 for(var key in data){
                   if (key==="text" || key==="intents" || key==="entities") {
                     if(key==="intents"){
                       data[key].forEach(function(i){
                         for(var a in i ){
                           if(a==="name"){
                             var json1={
                               name:i[a]
                             }
                             arr.push(json1)
                           }

                         }
                       })
                     }
                     else if (key==="entities") {
                       for(var b in data[key]){
                         var sri=data[key][b]
                         if(b==="who_needs_help:who_needs_help"){
                           sri.forEach(function(j){
                             for(var c in j){

                               if(c==="body"){
                                 var json3={
                                   body:j[c]
                                 }
                                 arr.push(json3)
                               }


                             }
                           })
                         }
                         if(b==="wit$location:location"){
                           sri.forEach(function(j){
                             for(var c in j){
                               if(c==="body" || c==="resolved"){
                                 if(c==="resolved"){
                                   for(var d in j[c]){
                                     var rag=j[c][d]
                                     rag.forEach(function(l){

                                       for(var m in l){

                                           if(m==="coords"){
                                             var json4={
                                               coords:l[m]
                                             }
                                             arr.push(json4)
                                           }
                                           if(m==="name"){
                                             var json5={
                                               body:l[m]
                                             }
                                             arr.push(json5)
                                           }
                                         }
                                     })

                                   }
                                 }
                               }


                             }
                           })
                         }
                       }

                     }
                     else{
                       var json6={
                         text:data[key]
                       }
                       arr.push(json6);
                     }
                   }
               }
             })
             .catch(console.error);
            }

          }
        })

        // for (var i = 0; i < 10; i++) {
          // var mystr=final[i].text;
          res.send(arr)

        // var jsobject={data:arr};
        // res.send(jsobject)
        // res.render("index",{reload:arr});
        // res.render('index', {
        //   jsonData: arr
        // });
})
})
// app.get("/post/:postName",function(req,res){
//   T.get('search/tweets', { q: '#chennai floods', count: 10}, function(err, result, response) {
//         var final=result.statuses
//         var title=_.lowerCase(req.params.postName);
//
//         for (var i = 0; i < 10; i++) {
//           var mystr=final[i].text
//           client.message(mystr, {})
//           .then((data) => {
//
//             arr.push(data);
//             if(arr.length>10){
//               arr.pop();
//             }
//
//         })
//         .catch(console.error);
//         }
//         arr.forEach(function(item){
//           for(var key in item){
//             if (title===_.lowerCase(key)) {
//               console.log();
//                 // res.send(item[key]);
//             }
//           }
//         })
//
//         // res.send(arr)
//         // res.render("index",{reload:arr});
// });
// });


let port=process.env.PORT;
if (port==null||port=="") {
  port=3000;
}
app.listen(port, () => {
  console.log(`App listening on port 3000!`)
})
