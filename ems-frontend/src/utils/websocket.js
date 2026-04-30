import SockJS from 'sockjs-client'
import Stomp from 'stompjs'

class WebSocketClient {
  constructor() {
    this.stompClient = null
    this.connected = false
    this.subscribers = {}
    this.pendingSubscriptions = []
    this.reconnectTimer = null
  }

  connect(onConnected) {
    const socket = new SockJS('/ws')
    this.stompClient = Stomp.over(socket)
    this.stompClient.debug = null // 关闭调试日志

    this.stompClient.connect({}, () => {
      this.connected = true
      console.log('WebSocket connected')

      // 重连后恢复之前的订阅
      this.pendingSubscriptions.forEach(({ destination, callback }) => {
        this._doSubscribe(destination, callback)
      })
      this.pendingSubscriptions = []

      if (onConnected) onConnected()
    }, (error) => {
      console.error('WebSocket error:', error)
      this.connected = false
      this.stompClient = null
      // 5秒后重连
      if (this.reconnectTimer) clearTimeout(this.reconnectTimer)
      this.reconnectTimer = setTimeout(() => this.connect(onConnected), 5000)
    })
  }

  _doSubscribe(destination, callback) {
    if (this.stompClient && this.connected) {
      this.subscribers[destination] = this.stompClient.subscribe(destination, (message) => {
        try {
          callback(JSON.parse(message.body))
        } catch (e) {
          callback(message.body)
        }
      })
    }
  }

  subscribe(destination, callback) {
    if (this.stompClient && this.connected) {
      this._doSubscribe(destination, callback)
    } else {
      // 连接未就绪，先缓存
      this.pendingSubscriptions.push({ destination, callback })
    }
  }

  unsubscribe(destination) {
    if (this.subscribers[destination]) {
      this.subscribers[destination].unsubscribe()
      delete this.subscribers[destination]
    }
  }

  disconnect() {
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer)
      this.reconnectTimer = null
    }
    if (this.stompClient) {
      try {
        this.stompClient.disconnect()
      } catch (e) {
        // ignore
      }
      this.stompClient = null
      this.connected = false
      this.subscribers = {}
    }
  }
}

export default new WebSocketClient()
