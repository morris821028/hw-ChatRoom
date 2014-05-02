前文
=====

計算機網路課程需實做 socket 相關的網路程式設計，根據課本附錄 JAVA 程式可以實作出一對多的 server-client 架構，但實際運用網路的軟體還必須處理更多網路層面的問題。因此本文想要做出一個基礎的聊天室程式，不只提供即時訊息傳遞，同時處理檔案上傳操作，更進一步探討如何運行 client-client 運行遊戲上的同步遊戲處理。

簡化遊戲設計，採用最簡單的貪食蛇對戰遊戲。程式將遭遇何時觸發遊戲、如何即時同步、如何應付不正常關閉處理。

介面設計
=====

![img](demo2.1.png)
上圖2.1為 client 端登入時的操作介面

使用者需要設定 Host IP Address 和 Host Port Number 進行連線，如果使用者提供的 Name 重複則會無法連線。而左下角提供密語選擇操作的選單，右上角提供遊戲連線的請求觸發，中間 block 提供 HTML 格式的處理視窗，但不提供使用者直接對其插入 HTML 語法，只能透過提供的轉換按鈕來發送 HTML 相關的訊息。
    
遊戲觸發時，會跳轉至分頁 Game 部分等待另一名使用者連入遊戲，連線成功並且在 5 秒後遊戲自動開始。密語觸發時，可以更進一步地在專屬頁面一對一溝通，但此時不提供 HTML 語法操作。

![img](demo2.2.png)
上圖2.2為 client 端登入時的操作介面

server 管理者需鍵入開啟連線的 port number，來讓使用者連入操作，並且啟動開啟 server OFF 鍵撥開。管理者可以自主發出廣播訊息，以及觀察所有使用者互相傳遞的資訊。
    
左方為檔案上傳開啟的 port number(暫時定為固定 port)，使用者藉由這個 port 傳送本地上傳的圖片，放來放置於圖片顯示功能，暫時無法由 server 軟體中操作圖片儲存的路徑位置，需修改程式內部變數。進一步想要在 server 端監控所有上傳資訊，並且提供管理員著手阻擋正在傳送的資料。相同地，可以對某些使用者進行權限控管。而在 Game List 區域中，可以監控當前哪位使用者正在等待遊戲請求。

需求設計
=====

## 同步運行多人連線 ##
根據server主機狀況不同，同時上線人數不可設置太大，因為是採用多線程的方式，當使用人數過多時，會造成嚴重的 delay。

## 密語轉送 ##
由 client 附加指定對象訊息，再由 server 向雙方傳送密語內容。由於轉送時一方 client 只知道對方的暱稱，所以在 server 限制連線時，需要求 client 提供不同於當前使用者的暱稱。

## 即時遊戲 ##

由 client 向 server 請求執行遊戲，並且等待 server 進行發送配對內容。發送成功後，由兩端 client 其中一方當作 game server 進行連線動作，之所以如此操作是因為沒有特殊幹線，在遊戲同步處理上會有相當大的 delay，因此希望由 client 自行負責。Client 不可藏於 NAT 中，此程式尚未設計突破 NAT 的操作。
   
遊戲雖然採用即時，但是事實上為非同步，也就是不會等待 server 都確認後進行資料處理，而是在 client 先著手進行資料處理，在最後 server 傳送另一方資料時，再進行判斷。沒有必要真正著手同步操作，稍微的 delay 對遊戲本身並沒有太大的影響，即便兩方同時收到錯誤的訊息，也就視同時獲勝的判斷，對於指定隨機對戰並無太大影響。

Client 溝通訊息上
=====

無法使用中文名稱正確進入 server，當前並非全部處理採用 utf-8 資料流，暫且只提供英文名稱的使用。
提供功能如下所列：

* 提供中文輸入
* 提供傳遞 URL 超連結
* 提供上傳 image URL 並且在窗口提供圖片瀏覽
* 提供上傳本地圖片，並且在窗口提供圖片瀏覽
* 遊戲操作說明
* 軟件的環境設置
* 遊戲指定的派發要求

安裝需求和步驟
=====
運行附錄 SocketServer.jar, SocketClient.jar可於任何有安裝 JAVA 7的電腦上運行操作。

由於開發時有第三方軟件，第三方軟體提供 html 語法的 parsing 和介面美化的用途，直接在 command 上運行編譯會稍有麻煩。
但可以直接在 Eclipse 上選擇 File -> Switch Workspace 指定到附錄的 CNworkspace 資料下運行編譯動作。

打包 .jar 操作：
可以參考 http://www.cnblogs.com/lanxuezaipiao/p/3291641.html
下載 Fat jar 

![img](demo3.1.png)
![img](demo3.2.png)
![img](demo3.3.png)

由於聊天室有本地上傳功能，此功能只限定在於 Server 有開啟 80 port，若有需要 Demo 用途，請使用有 apache 的主機進行 Server.jar 的開啟。

若為一般個人電腦，可下載 XAMPP，並且開啟軟件的 xampp-control.exe，將 apache 開啟，同時也要在 控制台\所有控制台項目\Windows 防火牆-> 進階設定 -> 輸入規則 -> 新增規則 -> 選取連接埠號(O) -> TCP, 特定本機連接埠(S) 80 -> 允許連線。開啟後並且重新確認 80 port 是否對外打開，之後便可以使用聊天式的本地上傳功能，會自動連線到 server 的 port 1979 並且上傳到 server 的 C:\xampp\htdocs\TEST\downloads 路徑下。
Server 端程式不會檢驗任何上傳的檔案，而上傳檔案也不會經由程式觸發運行，所有檔案命名方式採用 MD5 HASH 命名。

操作運行
=====
![img](demo5.1.jpg)
![img](demo5.2.jpg)
![img](demo5.3.jpg)
![img](demo5.4.jpg)
![img](demo5.5.jpg)
![img](demo5.6.jpg)
![img](demo5.7.jpg)
![img](demo5.8.jpg)
![img](demo5.9.jpg)
![img](demo5.10.png)
![img](demo5.11.png)
![img](demo5.12.png)
![img](demo5.13.png)
![img](demo5.14.png)
![img](demo5.15.png)
![img](demo5.16.png)
![img](demo5.17.png)
![img](demo5.18.png)

聯絡
=====
e-mail: morris821028@gmail.com