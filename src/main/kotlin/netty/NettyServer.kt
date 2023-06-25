package netty


import io.netty.bootstrap.Bootstrap
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.EventLoopGroup
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.DatagramPacket
import io.netty.channel.socket.nio.NioDatagramChannel
import io.netty.handler.logging.ByteBufFormat
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler
import io.netty.util.CharsetUtil
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


/**
 * netty server runner
 */
object NettyServer {

    private val workerGroup: EventLoopGroup = NioEventLoopGroup()
    fun start(port: Int = 6479) {
        Bootstrap().group(workerGroup).channel(NioDatagramChannel::class.java)
            .handler(LoggingHandler(LogLevel.TRACE, ByteBufFormat.SIMPLE))
            .handler(object : SimpleChannelInboundHandler<DatagramPacket>() {
                override fun channelRead0(ctx: ChannelHandlerContext, msg: DatagramPacket) {
                    //print

                    val srcMsg = "${
                        DateTimeFormatter.ISO_DATE_TIME.format(
                            LocalDateTime.now()
                        )
                    }, ${msg.sender()}, ${msg.content().toString(CharsetUtil.UTF_8)}"
                    println("receive：$srcMsg")
                    //echo
                    ctx.writeAndFlush(
                        DatagramPacket(
                            Unpooled.copiedBuffer(srcMsg, CharsetUtil.UTF_8), msg.sender()
                        )
                    ).sync()
                }
            }).also {
                it.bind(port).sync().channel().closeFuture().await()
            }
    }
}
