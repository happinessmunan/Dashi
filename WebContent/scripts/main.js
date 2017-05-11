(function() {

	/**
	 * Variables
	 */
	var user_id = '';
	var user_fullname = '';
	var lng = -122.08;
	var lat = 37.38;

	/**
	 * Initialize
	 */
	function init() {
		// Register event listeners
		$('login-btn').addEventListener('click', login);
		$('nearby-btn').addEventListener('click', loadNearbyRestaurants);
		$('fav-btn').addEventListener('click', loadFavoriteRestaurants);
		$('recommend-btn')
				.addEventListener('click', loadRecommendedRestaurants);

		// validateSession();

		onSessionValid({
			user_id : '1111',
			name : 'John Smith'
		});
	}

	/**
	 * Session
	 */
	function validateSession() {
		// The request parameters
		var url = './LoginServlet';
		var req = JSON.stringify({});

		// display loading message
		showLoadingMessage('Validating session...');

		// make AJAX call
		ajax('GET', url, req,
		// session is still valid
		function(res) {
			var result = JSON.parse(res);

			if (result.status === 'OK') {
				onSessionValid(result);
			}
		});
	}

	function onSessionValid(result) {
		user_id = result.user_id;
		user_fullname = result.name;

		var loginForm = $('login-form');
		var restaurantNav = $('restaurant-nav');
		var restaurantList = $('restaurant-list');
		var avatar = $('avatar');
		var welcomeMsg = $('welcome-msg');
		var logoutBtn = $('logout-link');

		welcomeMsg.innerHTML = 'Welcome, ' + user_fullname;

		showElement(restaurantNav);
		showElement(restaurantList);
		showElement(avatar);
		showElement(welcomeMsg);
		showElement(logoutBtn, 'inline-block');
		hideElement(loginForm);

		initGeoLocation();
	}

	function onSessionInvalid() {
		var loginForm = $('login-form');
		var restaurantNav = $('restaurant-nav');
		var restaurantList = $('restaurant-list');
		var avatar = $('avatar');
		var welcomeMsg = $('welcome-msg');
		var logoutBtn = $('logout-link');

		hideElement(restaurantNav);
		hideElement(restaurantList);
		hideElement(avatar);
		hideElement(logoutBtn);
		hideElement(welcomeMsg);

		showElement(loginForm);
	}

	function initGeoLocation() {
		if (navigator.geolocation) {
			navigator.geolocation.getCurrentPosition(onPositionUpdated,
					onLoadPositionFailed, {
						maximumAge : 60000
					});
			showLoadingMessage('Retrieving your location...');
		} else {
			onLoadPositionFailed();
		}
	}

	function onPositionUpdated(position) {
		lat = position.coords.latitude;
		lng = position.coords.longitude;

		loadNearbyRestaurants();
	}

	function onLoadPositionFailed() {
		console.warn('navigator.geolocation is not available');
		// loadNearbyRestaurants();
		getLocationFromIP();
	}

	function getLocationFromIP() {
		// Get location from http://ipinfo.io/json
		var url = 'http://ipinfo.io/json'
		var req = null;
		ajax('GET', url, req,
		// session is still valid
		function(res) {
			var result = JSON.parse(res);
			if ('loc' in result) {
				var loc = result.loc.split(',');
				lat = loc[0];
				lng = loc[1];
			} else {
				console.warn('Getting location by IP failed.');
			}
			loadNearbyRestaurants();
		});
	}

	// -----------------------------------
	// Login
	// -----------------------------------

	function login() {
		var username = $('username').value;
		var password = $('password').value;
		password = md5(username + md5(password));

		// The request parameters
		var url = './LoginServlet';
		var params = 'user_id=' + username + '&password=' + password;
		var req = JSON.stringify({});

		ajax('POST', url + '?' + params, req,
		// successful callback
		function(res) {
			var result = JSON.parse(res);

			// successfully logged in
			if (result.status === 'OK') {
				onSessionValid(result);
			}
		},
		// error
		function() {
			showLoginError();
		});
	}

	function showLoginError() {
		$('login-error').innerHTML = 'Invalid username or password';
	}

	function clearLoginError() {
		$('login-error').innerHTML = '';
	}

	// -----------------------------------
	// Helper Functions
	// -----------------------------------

	/**
	 * A helper function that makes a navigation button active
	 * 
	 * @param btnId -
	 *            The id of the navigation button
	 */
	function activeBtn(btnId) {
		var btns = document.getElementsByClassName('main-nav-btn');

		// deactivate all navigation buttons
		for (var i = 0; i < btns.length; i++) {
			btns[i].className = btns[i].className.replace(/\bactive\b/, '');
		}

		// active the one that has id = btnId
		var btn = $(btnId);
		btn.className += ' active';
	}

	function showLoadingMessage(msg) {
		var restaurantList = $('restaurant-list');
		restaurantList.innerHTML = '<p class="notice"><i class="fa fa-spinner fa-spin"></i> '
				+ msg + '</p>';
	}

	function showWarningMessage(msg) {
		var restaurantList = $('restaurant-list');
		restaurantList.innerHTML = '<p class="notice"><i class="fa fa-exclamation-triangle"></i> '
				+ msg + '</p>';
	}

	function showErrorMessage(msg) {
		var restaurantList = $('restaurant-list');
		restaurantList.innerHTML = '<p class="notice"><i class="fa fa-exclamation-circle"></i> '
				+ msg + '</p>';
	}

	/**
	 * A helper function that creates a DOM element <tag options...>
	 * 
	 * @param tag
	 * @param options
	 * @returns
	 */
	function $(tag, options) {
		if (!options) {
			return document.getElementById(tag);
		}

		var element = document.createElement(tag);

		for ( var option in options) {
			if (options.hasOwnProperty(option)) {
				element[option] = options[option];
			}
		}

		return element;
	}

	function hideElement(element) {
		element.style.display = 'none';
	}

	function showElement(element, style) {
		var displayStyle = style ? style : 'block';
		element.style.display = displayStyle;
	}

	/**
	 * AJAX helper
	 * 
	 * @param method -
	 *            GET|POST|PUT|DELETE
	 * @param url -
	 *            API end point
	 * @param callback -
	 *            This the successful callback
	 * @param errorHandler -
	 *            This is the failed callback
	 */
	function ajax(method, url, data, callback, errorHandler) {
		var xhr = new XMLHttpRequest();

		xhr.open(method, url, true);

		xhr.onload = function() {
			switch (xhr.status) {
			case 200:
				callback(xhr.responseText);
				break;
			case 403:
				onSessionInvalid();
				break;
			case 401:
				errorHandler();
				break;
			}
		};

		xhr.onerror = function() {
			console.error("The request couldn't be completed.");
			errorHandler();
		};

		if (data === null) {
			xhr.send();
		} else {
			xhr.setRequestHeader("Content-Type",
					"application/json;charset=utf-8");
			xhr.send(data);
		}
	}

	// -------------------------------------
	// AJAX call server-side APIs
	// -------------------------------------

	/**
	 * API #1 Load the nearby restaurants API end point: [GET]
	 * /Dashi/restaurants?user_id=1111&lat=37.38&lon=-122.08
	 */
	function loadNearbyRestaurants() {
		console.log('loadNearbyRestaurants');
		activeBtn('nearby-btn');

		// The request parameters
		var url = './restaurants';
		var params = 'user_id=' + user_id + '&lat=' + lat + '&lon=' + lng;
		var req = JSON.stringify({});

		// display loading message
		showLoadingMessage('Loading nearby restaurants...');

		// make AJAX call
		ajax('GET', url + '?' + params, req,
		// successful callback
		function(res) {
			var restaurants = JSON.parse(res);
			if (!restaurants || restaurants.length === 0) {
				showWarningMessage('No nearby restaurant.');
			} else {
				listRestaurants(restaurants);
			}
		},
		// failed callback
		function() {
			showErrorMessage('Cannot load nearby restaurants.');
		});
	}

	/**
	 * API #2 Load favorite (or visited) restaurants API end point: [GET]
	 * /Dashi/history?user_id=1111
	 */
	function loadFavoriteRestaurants(event) {
		event.preventDefault();
		activeBtn('fav-btn');

		// The request parameters
		var url = './history';
		var params = 'user_id=' + user_id;
		var req = JSON.stringify({});

		// display loading message
		showLoadingMessage('Loading favorite restaurants...');

		// make AJAX call
		ajax('GET', url + '?' + params, req, function(res) {
			var restaurants = JSON.parse(res);
			if (!restaurants || restaurants.length === 0) {
				showWarningMessage('No favorite restaurant.');
			} else {
				listRestaurants(restaurants);
			}
		}, function() {
			showErrorMessage('Cannot load favorite restaurants.');
		});
	}

	/**
	 * API #3 Load recommended restaurants API end point: [GET]
	 * /Dashi/recommendation?user_id=1111
	 */
	function loadRecommendedRestaurants() {
		activeBtn('recommend-btn');

		// The request parameters
		var url = './recommendation';
		var params = 'user_id=' + user_id;
		var req = JSON.stringify({});

		// display loading message
		showLoadingMessage('Loading recommended restaurants...');

		// make AJAX call
		ajax(
				'GET',
				url + '?' + params,
				req,
				// successful callback
				function(res) {
					var restaurants = JSON.parse(res);
					if (!restaurants || restaurants.length === 0) {
						showWarningMessage('No recommended restaurant. Make sure you have favorites.');
					} else {
						listRestaurants(restaurants);
					}
				},
				// failed callback
				function() {
					showErrorMessage('Cannot load recommended restaurants.');
				});
	}

	/**
	 * API #4 Toggle favorite (or visited) restaurants
	 * 
	 * @param business_id -
	 *            The restaurant business id
	 * 
	 * API end point: [POST]/[DELETE] /Dashi/history request json data: {
	 * user_id: 1111, visited: [a_list_of_business_ids] }
	 */
	function changeFavoriteRestaurant(business_id) {
		// Check whether this restaurant has been visited or not
		var li = $('restaurant-' + business_id);
		var favIcon = $('fav-icon-' + business_id);
		var isVisited = li.dataset.visited !== 'true';

		// The request parameters
		var url = './history';
		var req = JSON.stringify({
			user_id : user_id,
			visited : [ business_id ]
		});
		var method = isVisited ? 'POST' : 'DELETE';

		ajax(method, url, req,
				// successful callback
				function(res) {
					var result = JSON.parse(res);
					if (result.status === 'OK') {
						li.dataset.visited = isVisited;
						favIcon.className = isVisited ? 'fa fa-heart'
								: 'fa fa-heart-o';
					}
				});
	}

	// -------------------------------------
	// Create restaurant list
	// -------------------------------------

	/**
	 * List restaurants
	 * 
	 * @param restaurants -
	 *            An array of restaurant JSON objects
	 */
	function listRestaurants(restaurants) {
		// Clear the current results
		var restaurantList = $('restaurant-list');
		restaurantList.innerHTML = '';

		for (var i = 0; i < restaurants.length; i++) {
			addRestaurant(restaurantList, restaurants[i]);
		}
	}

	/**
	 * Add restaurant to the list
	 * 
	 * @param restaurantList -
	 *            The
	 *            <ul id="restaurant-list">
	 *            tag
	 * @param restaurant -
	 *            The restaurant data (JSON object)
	 */
	function addRestaurant(restaurantList, restaurant) {
		var business_id = restaurant.business_id;

		// create the <li> tag and specify the id and class attributes
		var li = $('li', {
			id : 'restaurant-' + business_id,
			className : 'restaurant'
		});

		// set the data attribute
		li.dataset.business = business_id;
		li.dataset.visited = restaurant.is_visited;

		// restaurant image
		li.appendChild($('img', {
			src : restaurant.image_url
		}));

		// section
		var section = $('div', {});

		// title
		var title = $('a', {
			href : restaurant.url,
			target : '_blank',
			className : 'restaurant-name'
		});
		title.innerHTML = restaurant.name;
		section.appendChild(title);

		// category
		var category = $('p', {
			className : 'restaurant-category'
		});
		category.innerHTML = 'Category: ' + restaurant.categories.join(', ');
		section.appendChild(category);

		// stars
		var stars = $('div', {
			className : 'stars'
		});
		for (var i = 0; i < restaurant.stars; i++) {
			var star = $('i', {
				className : 'fa fa-star'
			});
			stars.appendChild(star);
		}

		if (('' + restaurant.stars).match(/\.5$/)) {
			stars.appendChild($('i', {
				className : 'fa fa-star-half-o'
			}));
		}

		section.appendChild(stars);

		li.appendChild(section);

		// address
		var address = $('p', {
			className : 'restaurant-address'
		});

		address.innerHTML = restaurant.full_address.replace(/,/g, '<br/>');
		li.appendChild(address);

		// favorite link
		var favLink = $('p', {
			className : 'fav-link'
		});

		favLink.onclick = function() {
			changeFavoriteRestaurant(business_id);
		};

		favLink.appendChild($('i', {
			id : 'fav-icon-' + business_id,
			className : restaurant.is_visited ? 'fa fa-heart' : 'fa fa-heart-o'
		}));

		li.appendChild(favLink);

		restaurantList.appendChild(li);
	}

	init();

})();

// END
