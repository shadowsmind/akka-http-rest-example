package example.common.domain

import example.api.protocol.ApiJsonProtocol
import example.common.utils.{ DateHelper, SecurityHelper }

object accounts {

  case class Account(
    id:             Option[Long]     = None,
    createdAt:      DateTime         = DateHelper.now,
    updatedAt:      Option[DateTime] = None,
    status:         EntityStatus     = EntityStatus.Active,
    role:           UserRole         = UserRole.User,
    nickName:       String,
    email:          String,
    emailConfirmed: Boolean          = false,
    password:       String
  ) extends BaseEntityWithStatus with HasRole

  case class RegisterDto(nickName: String, email: String, password: String, passwordRepeat: String)

  case class AccountUpdateDto(nickName: String)

  case class AccountDto(
    id:        Long,
    createdAt: Long,
    status:    EntityStatus,
    role:      UserRole,
    nickName:  String
  )

  trait AccountsJsonProtocol extends ApiJsonProtocol {
    implicit val registerFormat = jsonFormat(RegisterDto, "nick_name", "email", "password", "password_repeat")
    implicit val accountUpdateFormat = jsonFormat(AccountUpdateDto, "nick_name")
    implicit val accountDtoFormat = jsonFormat(AccountDto, "id", "created_at", "status", "role", "nick_name")

    implicit val accountDtoResponseFormat = apiResponseFormat[AccountDto]
  }

  object AccountsJsonProtocol extends AccountsJsonProtocol

  object AccountConverter {

    def toDto(entity: Account): AccountDto = {
      AccountDto(
        id        = entity.id.get,
        createdAt = DateHelper.toTimestamp(entity.createdAt),
        status    = entity.status,
        role      = entity.role,
        nickName  = entity.nickName
      )
    }

    def toEntity(dto: RegisterDto): Account = {
      Account(
        nickName = dto.nickName,
        email    = dto.email,
        password = SecurityHelper.hashPassword(dto.password)
      )
    }

  }

}
