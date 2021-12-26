<%@ page contentType="text/html; charset=UTF-8" %>

<nav class="navbar navbar-expand-lg navbar-light" style="background-color: #e3f2fd;">
	<a class="navbar-brand" href="testvir-dep">
	  <img src="../theme/icon.png" width="30" height="30" alt=""/>
	  Vigie Covid-19
	</a>
	<button class="navbar-toggler" type="button" data-toggle="collapse"
	    data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent"
	    aria-expanded="false" aria-label="Toggle navigation">
		<span class="navbar-toggler-icon"></span>
	</button>

	<div class="collapse navbar-collapse" id="navbarSupportedContent">
		<ul class="navbar-nav mr-auto">
			<li class="nav-item dropdown">
				<a class="nav-link dropdown-toggle" href="#" role="button" data-toggle="dropdown"
				    aria-haspopup="true" aria-expanded="false">
				Données Hospitalières
				</a>
				<div class="dropdown-menu">
					<a class="dropdown-item" href="dh-dc">Décès</a>
					<a class="dropdown-item" href="dh-hosp">Hospitalisations</a>
					<a class="dropdown-item" href="dh-rea">Réanimations</a>
					<a class="dropdown-item" href="dh-correl">Corrélations Réa / Hosp.</a>
					<a class="dropdown-item" href="dh-ages">Tranches d'ages</a>
					<a class="dropdown-item" href="dh-repro">Nombre de reproduction</a>
				</div>
			</li>

			<li class="nav-item dropdown">
				<a class="nav-link dropdown-toggle" href="#" role="button" data-toggle="dropdown"
				    aria-haspopup="true" aria-expanded="false">
				Données des urgences
				</a>
				<div class="dropdown-menu">
					<a class="dropdown-item" href="sursau-day">Données quotidiennes</a>
					<a class="dropdown-item" href="sursau-dep">Appels SOS Médecins par département</a>
				</div>
			</li>

			<li class="nav-item dropdown">
				<a class="nav-link dropdown-toggle" href="#" role="button" data-toggle="dropdown"
				    aria-haspopup="true" aria-expanded="false">
				Taux d'incidence
				</a>
				<div class="dropdown-menu">
					<a class="dropdown-item" href="testvir-day">Données quotidiennes en métropole</a>
					<a class="dropdown-item" href="testvir-dep">Données par département</a>
				</div>
			</li>

			<li class="nav-item">
				<a class="nav-link" href="vacsi">Vaccination<span class="sr-only">(current)</span></a>
			</li>

		</ul>
	</div>

</nav>
