package com.juliaosystem.api.controller

import com.common.lib.api.controller.CrudController
import com.common.lib.api.dtos.request.AuditRequest
import com.common.lib.api.dtos.request.LoginDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import com.common.lib.api.dtos.request.RegisterUserDTO
import com.common.lib.api.response.PlantillaResponse
import com.juliaosystem.infrastructure.services.primary.UserService
import com.common.lib.utils.errors.AbtractError
import com.juliaosystem.utils.jtw.exeption.BussinesRuleException
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/user")
@Tag(name = "usuarios", description = "Endpoints relacionados con el manejo de usuarios")
class UserController(
    private val userService: UserService,
    private val abtractError: AbtractError
) : CrudController<RegisterUserDTO, RegisterUserDTO, _root_ide_package_.com.common.lib.infraestructure.entitis.User, UUID> {



    @Operation(summary = "Logear usuarios", description = "Permite iniciar sesion")
    @PostMapping(value = ["/login"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun login(@RequestBody loginDTO: LoginDTO): ResponseEntity<PlantillaResponse<RegisterUserDTO>> {
        val response = userService.login(loginDTO).orElseThrow { NoSuchElementException() }
        return ResponseEntity(response, response.httpStatus)
    }

    @GetMapping("/roles")
    fun getRoles(@RequestHeader("Authorization") authHeader: String): ResponseEntity<Map<String, Int>> {
        val hashMap = userService.getRoles(authHeader)
        return ResponseEntity.ok(hashMap)
    }

    @Operation(summary = "valid", description = "Permite validar autorizaciones")
    @GetMapping("/valid")
    fun valid(@RequestHeader("Authorization") authHeader: String): ResponseEntity<HashMap<String, String>> {
        return try {
            userService.checkValidity(authHeader)
            val hashMap = hashMapOf("is_valid" to "true")
            ResponseEntity.ok(hashMap)
        } catch (e: Exception) {
            throw BussinesRuleException("is_valid", "False", HttpStatus.FORBIDDEN)
        }
    }

    @PostMapping(value = ["/logout"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun logout(@RequestParam(value = "refresh_token") refreshToken: String): ResponseEntity<*> {
        val logout = "logout"
        return try {
            userService.logout(refreshToken)
            val hashMap = hashMapOf(logout to "true")
            ResponseEntity.ok(hashMap)
        } catch (e: Exception) {
            abtractError.logError(e)
            throw BussinesRuleException(logout, "False", HttpStatus.FORBIDDEN)
        }
    }

    @PostMapping(value = ["/refresh"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun refresh(@RequestParam(value = "refresh_token") refreshToken: String): ResponseEntity<String> {
        return try {
            ResponseEntity.ok(userService.refresh(refreshToken))
        } catch (e: Exception) {
            abtractError.logError(e)
            throw BussinesRuleException("refresh", "False", HttpStatus.FORBIDDEN)
        }
    }


    @Operation(summary = "add", description = "Permite agregar un usuario")
    override fun add(
        entidad: RegisterUserDTO,
        id: UUID?,
        ip: String,
        dominio: String,
        usuario: String,
        idBussines: Long,
        proceso: String
    ): ResponseEntity<PlantillaResponse<RegisterUserDTO>> {

        val audit=  AuditRequest.builder()
            .ip(ip).dominio(dominio)
            .proceso(proceso).usuario(usuario).
            idBussines(idBussines)
            .build()

        val response = userService.add(entidad, audit)
        return ResponseEntity(response, response.httpStatus)
    }

    @Operation(summary = "all users", description = "permite obtener lista de usuarios ")
   override fun all(
         id : UUID?,
         ip: String,
        dominio: String,
        usuario: String,
         idBussines: Long,
        proceso: String,
    ): ResponseEntity<PlantillaResponse<RegisterUserDTO>> {

     val audit=  AuditRequest.builder()
                       .ip(ip).dominio(dominio)
                      .proceso(proceso).usuario(usuario).
                      idBussines(idBussines)
           .build()
       val response = userService.getUsers(id, idBussines, audit)
        return ResponseEntity(response,response.httpStatus)
    }

}
