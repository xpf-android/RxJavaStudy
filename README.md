01.RxJava概念与观察者设计模式。  
- 1.起点 和 终点，一旦满足 起点 和 终点 这样的需求，都可以使用RxJava来实现。  
- 2.标准中的观察者设计模式，一个被观察者 ---- 多个观察者 多次注册。  
- 3.RxJava是改装的观察者设计模式，一个订阅(注册) 一个观察者。  
--------------------------------------------------------------------------
02.RxJava上游和下游。  
- 1.上游 Observable 被观察者， 下游 Observer 观察者。  
- 2.ObservableEmitter<Integer> emitter 发射器 发射事件。    
- 3.拆分来写的，链式调用。  
- 4.RxJava流程1，大致流程，参考MainActivity的test4方法代码    
- 5.RxJava流程2，3个结论。 参考MainActivity的test5方法代码     
- 6.RxJava切断下游，让下游不再接收上游发射的事件。参考MainActivity的test6方法代码      
----------------------------------------------------------------------------
03.RxJava创建型操作符 -- 专门创建 被观察者/上游/Observable  
- 观察者：下游，接收事件  完整版本Observer  简化版Consumer  
- create：使用者自己发射事件  
- just 内部自己发射的，单一对象  
- fromArray 内部自己发射的，数集对象  
- empty：内部自己发射的 ，下游默认是Object，无法发出有值事件，只会发射 onComplete  
- range：内部自己发射的，start 1 累加   count 5    最后结果：1 2 3 4 5    
----------------------------------------------------------------------------  
04.RxJava变换型操作符。  
上游  ------->    变换操作(往右边流向的时候，进行变换)  ---------->  下游  
- 1.map       把上一层Int  Int变换String                                           观察者String类型。  
- 2.flatMap   把上一层Int  Int变换ObservableSource<String>{还可以再次发射多次事件}   观察者String类型。 不排序的  
- 3.concatMap 把上一层Int  Int变换ObservableSource<Bitmap>{还可以再次发射多次事件}   观察者Bitmap类型。 排序的  
- 4.groupBy   把上一层Int  Int变换String(高端配置电脑)     观察者GroupedObservable类型 {key="高端", 细节再包裹一层}  
- 5.buffer    100个事件 Integer     .buffer(20)    观察者List<Integer>==五个集合  
