01.RxJava概念与观察者设计模式    
- 1.起点 和 终点，一旦满足 起点 和 终点 这样的需求，都可以使用RxJava来实现。  
- 2.标准中的观察者设计模式，一个被观察者 ---- 多个观察者 多次注册。  
- 3.RxJava是改装的观察者设计模式，一个订阅(注册) 一个观察者。  
--------------------------------------------------------------------------
02.RxJava上游和下游    
- 1.上游 Observable 被观察者， 下游 Observer 观察者。  
- 2.ObservableEmitter<Integer> emitter 发射器 发射事件。    
- 3.拆分来写的，链式调用。  
- 4.RxJava流程1，大致流程，参考MainActivity的test4方法代码    
- 5.RxJava流程2，3个结论。 参考MainActivity的test5方法代码     
- 6.RxJava切断下游，让下游不再接收上游发射的事件。参考MainActivity的test6方法代码      
----------------------------------------------------------------------------
03.RxJava创建型操作符   
- 专门创建 被观察者/上游/Observable  
- 观察者：下游，接收事件  完整版本Observer  简化版Consumer  
- create：使用者自己发射事件  
- just 内部自己发射的，单一对象  
- fromArray 内部自己发射的，数集对象  
- empty：内部自己发射的 ，下游默认是Object，无法发出有值事件，只会发射 onComplete  
- range：内部自己发射的，start 1 累加   count 5    最后结果：1 2 3 4 5    
----------------------------------------------------------------------------  
04.RxJava变换型操作符    
上游  ------->    变换操作(往右边流向的时候，进行变换)  ---------->  下游  
- 1.map       把上一层Int  Int变换String                                           观察者String类型。  
- 2.flatMap   把上一层Int  Int变换ObservableSource<String>{还可以再次发射多次事件}   观察者String类型。 不排序的  
- 3.concatMap 把上一层Int  Int变换ObservableSource<Bitmap>{还可以再次发射多次事件}   观察者Bitmap类型。 排序的  
- 4.groupBy   把上一层Int  Int变换String(高端配置电脑)     观察者GroupedObservable类型 {key="高端", 细节再包裹一层}  
- 5.buffer    100个事件 Integer     .buffer(20)    观察者List<Integer>==五个集合  
---------------------------------------------------------------------------------  
05.RxJava过滤型操作符  
上游  ------->    过滤操作(往右边流向的时候，进行过滤)  ---------->  下游  
- 1.filter 如果是false全部都发射给下游，如果是true，全部都不发射给下游。  
- 2.take ：只有再定时器运行基础上 加入take过滤操作符，才有take过滤操作符的价值。  
- 3.distinct过滤重复事件。  
- 4.elementAt 指定发射事件内容，如果无法指定，有默认的事件。  
-------------------------------------------------------------------------------  
06.RxJava条件型操作符      
上游  ------->    条件操作(往右边流向的时候，条件判断)  ---------->  下游  
操作符：RxJava说的很神奇，API的调用， RxJava改变开发者的思维  
RxJava == Java编程  
语法 == 操作符API  
所有的操作符都学会了，才能真正的证明把RxJava的使用学会了 == Java所有的语法学会，Java入门  
RxJava作者: Android之神 2010 开源过Android开源的框架库， RxJava巅峰之作  

- All: 如同 if 那样的功能 ：全部为true，才是true，只要有一个为false，就是false.  
- contains 是否包含  
- any 全部为 false，才是false， 只要有一个为true，就是true  
- 如果使用了条件操作符，下一层，接收的类型 就是条件类型(Boolean)  
--------------------------------------------------------------------------------  
07.RxJava合并型操作符  
两个或者多个 被观察者 合并。  
- 1.startWait，concatWith ：先创建被观察者，然后再组合其他的被观察者，然后再订阅  
- 2.concat/merge/zip：直接合并多个被观察者，然后订阅  
细节：  
- startWait 先执行 startWait括号里面的被观察者  
- concatWait 后执行 concatWait括号里面的被观察者  
- concat 是按照顺序依次执行 最多四个被观察者进行合并  
- merge 并列执行的，（演示并列的执行，所以学了intervalRange） 最多四个被观察者进行合并(成一个)  
- zip 需要对应关系 需要对应，如果不对应，会被忽略的， 最多9个被观察者 进行合并(成一个)  
----------------------------------------------------------------------------------------
08.RxJava异常处理操作符  
- 1.RxJava中是不标准的throw new IllegalAccessError("我要报错了");  
- 2. RxJava标准的e.onError(XXX);  
- 3.onErrorReturn最先拦截到e.onError并且可以给下游返回一个 标识400,   throw new  XXX 拦截不到，整个程序奔溃  
- 4.onErrorResumeNext最先拦截到e.onError并且可以给下游返回一个 被观察者（还可以再次发送）,   throw new  XXX 拦截不到，整个程序奔溃  
- 5.onExceptionResumeNext 能在发生异常的时候，扭转乾坤，能够处理 throw new  XXX，可以真正的让App不奔溃  
- 6.retry return false; 代表不去重试  return true; 不停的重试，  演示二 重试次数，  演示三 打印重试了多少次，计数  