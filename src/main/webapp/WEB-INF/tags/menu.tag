<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<!--  >%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%-->
<%@ attribute name="name" required="true" rtexprvalue="true"
	description="Name of the active menu: home, owners, vets or error"%>

<nav class="navbar navbar-default" role="navigation">
	<div class="container">
		<div class="navbar-header">
				<a href="/"> 
                	<img src="/resources/images/idus_martii-logo.png" width="70" height="75"/>                            
                </a>
			<button type="button" class="navbar-toggle" data-toggle="collapse"
				data-target="#main-navbar">
				<span class="sr-only"><os-p>Toggle navigation</os-p></span> <span
					class="icon-bar"></span> <span class="icon-bar"></span> <span
					class="icon-bar"></span>
			</button>
		</div>
		<div class="navbar-collapse collapse" id="main-navbar">
			<ul class="nav navbar-nav">
				
				<sec:authorize access="hasAnyAuthority('admin')">

					<petclinic:menuItem active="${name eq 'achievements'}" url="/achievements"
						title="achievements">										
						<span class="glyphicon glyphicon-flag" aria-hidden="true"></span>
						<span>Achievements</span>	
					</petclinic:menuItem>

				</sec:authorize>
				
				<sec:authorize access="hasAnyAuthority('player')">

					<petclinic:menuItem active="${name eq 'achievements'}" url="/achievements/player" 
						title="achievements">										
						<span class="glyphicon glyphicon-flag" aria-hidden="true"></span>
						<span>Achievements</span>	
					</petclinic:menuItem>

					<petclinic:menuItem active="${name eq 'invitations'}" url="/invitations"
						title="invitations">
						<span class="glyphicon glyphicon-envelope" aria-hidden="true"></span>
						<span>Invitations</span>
					</petclinic:menuItem>

					<petclinic:menuItem active="${name eq 'friends'}" url="/friends"
						title="friends">
						<span class="glyphicon glyphicon-heart" aria-hidden="true"></span>
						<span>Friends</span>
					</petclinic:menuItem>

					<petclinic:menuItem active="${name eq 'statistics'}" url="/statistics"
						title="statistics">										
						<span class="glyphicon glyphicon-stats" aria-hidden="true"></span>
						<span>Statistics</span>	
					</petclinic:menuItem>

					<petclinic:menuItem active="${name eq 'ranking'}" url="/ranking"
						title="ranking">										
						<span class="glyphicon glyphicon-signal" aria-hidden="true"></span>
						<span>Ranking</span>	
					</petclinic:menuItem>

				</sec:authorize>

			</ul>




			<ul class="nav navbar-nav navbar-right">
				<sec:authorize access="!isAuthenticated()">
					<li><a href="<c:url value="/login" />">Login</a></li>
					<li><a href="<c:url value="/players/register"/>">Register</a></li>
				</sec:authorize>
				<sec:authorize access="isAuthenticated()">
					<li class="dropdown"><a href="#" class="dropdown-toggle"
						data-toggle="dropdown"> <span class="glyphicon glyphicon-user"></span>
							<strong><sec:authentication property="name" /></strong> <span
							class="glyphicon glyphicon-chevron-down"></span>
					</a>
						<ul class="dropdown-menu">
							<li>
								<div class="navbar-login">
									<div class="row">
										<div class="col-lg-4">
											<p class="text-center">
												<span class="glyphicon glyphicon-user icon-size"></span>
											</p>
										</div>
										<div class="col-lg-8">
											<p class="text-left">
												<strong><sec:authentication property="name" /></strong>
											</p>
											<p class="text-left">
												<a href="<c:url value="/logout" />"
													class="btn btn-primary btn-block btn-sm">Logout</a>
											</p>
										</div>
									</div>
								</div>
							</li>
							<li class="divider"></li>
<!-- 							
                            <li> 
								<div class="navbar-login navbar-login-session">
									<div class="row">
										<div class="col-lg-12">
											<p>
												<a href="#" class="btn btn-primary btn-block">My Profile</a>
												<a href="#" class="btn btn-danger btn-block">Change
													Password</a>
											</p>
										</div>
									</div>
								</div>
							</li>
-->
						</ul></li>
				</sec:authorize>
			</ul>
		</div>



	</div>
</nav>
