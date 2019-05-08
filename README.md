# fabric_java
项目是一个包含fabric_javasdk,apiServer,eventServer的全Hyperledger Fabric客户端项目。
基于1.4版本的fabric 封装。


# fabric_java_sdk
fabric_java_sdk 是基于  IBM封装Hyperledger Fabric 开源sdk项目的封装。 
sdk 具体使用说明：

环境要求：JDK1.8 
Api_server使用说明:
目前jar包在类FBUtil中对外提供一个方法：init，
FBClient类中提供query，invoke。


1.init函数：
作用：在调用区块链网络进行CRUD操作之前需要实例化当前客户端。建议在项目启动完成后调用该函数，向网络表明当前用户的身份。
该对象的构建依赖于配置文件，文件默认放在src/main/resources,文件名称client_sdk.yaml。
 client_sdk.yaml 文件说明:


orderers: #order节点配置
  orderer0: #自定义order节点名称
    host: orderer.example.com:7050 #order对应的host，需要端口号
    useTLS: true #是否启用tls
    tlsPath: src/test/crypto-config/ordererOrganizations/example.com/orderers/orderer.example.com/tls/server.crt #order对应的tls证书地址，相对路径。 如果userTLS为false，此项为空
peers: #peer节点配置
  peer01: #自定义peer名称
    host: peer0.org1.example.com:7051 #peer节点对应的host，需要携带端口号
    orgName: org1 #peer节点对应的组织名称
    useTLS: true #节点是否开启TLS认证
    tlsPath: src/test/crypto-config/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/server.crt #peer节点对应的TLS 证书地址，相对路径。 如果userTLS为false，此项为空
  peer02: #通peer01配置说明
    host: peer0.org2.example.com:9051
    orgName: org2
    useTLS: true
    tlsPath: src/test/crypto-config/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/tls/server.crt
eventPeers: #监听触发event的peer节点信息（目前apiserver和eventserver分开，此配置apiserver用不到）
  peer0:
    host: peer0.org1.example.com:7051
    orgName: org1
    useTLS: true
    tlsPath: /opt/gopath/src/github.com/peersafe/worktool/crypto-config/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/server.crt
channels: #网络中的channel说明
  mspConfigPath: src/test/crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp #启动的client的对应msp文件路径
  localMspId:          Org1MSP #组织配置的mspId
  channelInfo: #具体的channel信息
    mychannel:  #the first channel name
      chaincodes:
        factor: #the first chaincode name installed in channel1
          chaincodeVersion:    1.0 #链码版本
          chaincodePolicy:
            orgs:
            - org1
            - org2
            rule: and #背书策略
eventchannels: #监听的channel，apiserver暂时用不到
  mychannel:
    chaincodes:
    - factor




示例（项目启动时调用）：
@Component
public class ApplicationStartup implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args)  throws Exception{
        System.out.println("系统启动执行！");
        FBUtil.init();
    }
}


2.invoke函数：
作用：调用区块链网络进行需要达成共识的操作接口，如新增。
参数说明：
参数名称	数据类型	是否必填	说明	备注
channelName	String	yes	通道名称	
chaincodeName	String	yes	链码名称	
funcName	String	yes	调用的链码中的方法名称	
args	List<String>	yes	链码中的方法的参数	


示例：

FBInvokeRes res =FBUtil.fb.invoke("mychannel","factor","SaveData", new String[]{JSONArray.toJSONString(object)});
返回对象（FBInvokeRes）说明：
熟悉名称	数据类型	说明	备注
isSuccess	Boolean	Invoke是否成功	
txId	String	交易ID	
msg	String	附加信息	
3.query函数：
作用：调用区块链网络进行查询的接口。
参数说明：
参数名称	数据类型	是否必填	说明	备注
channelName	String	yes	通道名称	
chaincodeName	String	yes	链码名称	
funcName	String	yes	调用的链码中的方法名称	
args	List<String>	yes	链码中的方法的参数	

示例：
String res =  FBUtil.fb.query(“mychannel”,"factor","QueryDataByFabricTxId",new String[] {fabrictxid});
返回的res是一个json格式的字符串，自行解析即可。



Event_server使用说明
系统启动调用：
1.调用方法 FBUtil.init()初始化客户端
2.调用FBUtil.fb.listen()开启监听。
Listen函数说明
参数说明：
参数名称	数据类型	说明	备注
queue
	BlockingQueue
	用于监听的队列	初始化时候注意队列大小，过大影响内存使用，过小可能导致接收不到


示例：

public void run(ApplicationArguments args)  throws Exception {
    System.out.println("系统启动执行 ！");
    FBUtil.init();
    BlockingQueue queue = new ArrayBlockingQueue(Integer.MAX_VALUE >> 12);//建议队列大小，该值情况下内存最多占用大概在60M
    FBUtil.fb.listen(queue);
    while (true) {
        String es =queue.take().toString();
        System.out.println(es);//此处进行业务处理
    }
}
关于队列返回的字符串 es 说明：
Json字符串。属性说明：
blockNumber 区块高度
chaincodeId 链码名称
eventName 监听的event名称
txId 交易ID
payload 调用时的参数信息
Eventhub event来源，如果是主动查询的话此处为空

# fabric_java_apiServer
基于上述sdk完成的一个完成的apiServer的封装。具备完整的客户端功能，可以直接从浏览器访问。

# fabric_java_eventServer
具有监听机制的客户端。对于发到order的交易请求，通过chaincode中的注册监听机制在这里进行接收，确保消息落账。






