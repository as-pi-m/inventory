package ai.brave.inventory.security.service

import ai.brave.inventory.security.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl(private val userRepository: UserRepository) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("User not found with username: $username")

        val authorities = user.roles.split(',')
            .map { SimpleGrantedAuthority(it.trim()) }

        return org.springframework.security.core.userdetails.User
            .withUsername(user.username)
            .password(user.password)
            .authorities(authorities)
            .build()
    }
}