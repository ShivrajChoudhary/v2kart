$(document).ready(function() {
	$('.navigationNodeParent > .ui-icon-plus').off().on('click', function() {
		$(this).toggleClass('navNode-icon-white-bg');
		$(this).siblings('.ui-btn-text').toggleClass('nav-home-menu-selector');
		$(this).siblings('.ui-btn-text').find('a').toggleClass('navNode-white-text');
	});
});