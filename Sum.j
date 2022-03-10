.class public Sum

.super java/lang/Object
.method public <init>()V
aload_0
invokenonvirtual java/lang/Object/<init>()V
return
.end method

;sum function
.method public static sum(I)I
.limit stack 32
.limit locals 8
ldc 1
istore 1
ldc 0
istore 2
     Loop:
       iload 1
       iload 2
       iadd
       istore 2

       iinc 1 1

       iload_0
       iload_1
       if_icmpge	 Loop
       iload 2
       ireturn

.end method

.method public static main([Ljava/lang/String;)V
.limit stack 32
.limit locals 8
getstatic java/lang/System/out Ljava/io/PrintStream;
astore 1
ldc 1
istore_2
     Loop:
       aload 1
       iload 2
       invokestatic Sum/sum(I)I
       invokevirtual java/io/PrintStream/println(I)V
       iinc 2 1
       ldc 100
       iload_2
       if_icmpge	 Loop
       return


.end method