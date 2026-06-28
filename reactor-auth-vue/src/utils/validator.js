export const validators = {
  // 用户名验证
  username: (rule, value, callback) => {
    if (!value) {
      callback(new Error('请输入用户名'))
    } else if (value.length < 3 || value.length > 20) {
      callback(new Error('用户名长度在3-20个字符之间'))
    } else if (!/^[a-zA-Z0-9_一-龥]+$/.test(value)) {
      callback(new Error('用户名只能包含字母、数字、下划线或中文'))
    } else {
      callback()
    }
  },

  // 密码验证
  password: (rule, value, callback) => {
    if (!value) {
      callback(new Error('请输入密码'))
    } else if (value.length < 6 || value.length > 20) {
      callback(new Error('密码长度在6-20个字符之间'))
    } else if (!/^(?=.*[a-zA-Z])(?=.*\d)/.test(value)) {
      callback(new Error('密码必须包含字母和数字'))
    } else {
      callback()
    }
  },

  // 邮箱验证
  email: (rule, value, callback) => {
    if (!value) {
      callback(new Error('请输入邮箱'))
    } else if (!/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(value)) {
      callback(new Error('请输入正确的邮箱格式'))
    } else {
      callback()
    }
  },

  // 手机号验证
  phone: (rule, value, callback) => {
    if (value && !/^1[3-9]\d{9}$/.test(value)) {
      callback(new Error('请输入正确的手机号'))
    } else {
      callback()
    }
  }
}
