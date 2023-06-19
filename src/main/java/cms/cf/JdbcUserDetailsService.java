package cms.cf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import cms.cf.subtitles.dao.impl.RoleDao;
import cms.cf.subtitles.dao.impl.UserDao;

@Service
public class JdbcUserDetailsService implements UserDetailsService {
	
	//precisa adicionar nos nomes das roles.
	//varios metodos da api http e spring adicionam isso ao comparar nomes de roles.
	//vc so precisa usar o prefixo cadastrar as roles com o usuario.
	public static final String ROLE_PREFIX = "ROLE_";
	
	@Override
	public UserDetails loadUserByUsername(String username) 
	throws UsernameNotFoundException 
	{
		cms.cf.subtitles.dao.vo.User u = null;
		ArrayList<String> roles = new ArrayList<>();
		
		try {
			u = UserDao.get(username);
			roles = RoleDao.getRoles(username);
			
		} catch (Exception e) {
			throw new UsernameNotFoundException("Usuário não encontrado com e-mail informado");
		}
		return new User(username, u.getPwd(), getAuthorities(roles) );
	}
	
	private Collection<GrantedAuthority> getAuthorities(ArrayList<String> roles) 
	{
		return roles.stream()
				.map(r -> new SimpleGrantedAuthority(ROLE_PREFIX+r))
				.collect(Collectors.toSet());
	}

}