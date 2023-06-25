import netty.NettyServer

fun main(args: Array<String>) {
    args.forEach {
        if (it.startsWith("-port=")) {
            NettyServer.start(it.removePrefix("-port=").toInt())

        }
    }
    NettyServer.start()
}
