log-view是一个slf4j日志分析GUI程序，使用javafx编写，主程序为log-view.jar， 运行需要在操作系统上安装java虚拟机。

程序的发布文件在release目下，log-view.jar是执行程序，log.xml是配置文件。

配置文件配置的是日志文件的目录，目录中不可以直接放置文本格式的日志文件，而应该放置子目录，子目录下再放置真正的日志文件

示例如下：

配置文件内容：  c:\\日志目录

那么对应的格式如下

-日志目录

&ensp;&ensp;&ensp;&ensp;-日志子目录1

&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;-日志文件1.txt

&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;-日志文件2.txt

&ensp;&ensp;&ensp;&ensp;-日志子目录2

&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;-日志文件1.txt

&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;-日志文件2.txt
