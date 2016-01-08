ACCMOB.carousel = {

	initialize: function ()
	{
		this.bindJCarousel();
	},
	bindJCarousel: function ()
	{
		
	}
}

$(window).load(function ()
{
	 $(".owl-carousel").owlCarousel({
		
		 navigation : true,
		 autoPlay : true,
		 slideSpeed : 300,
		 loop:true,
		 paginationSpeed : 400,
		 singleItem:true,
		 pagination:false,
		 rewindNav: true,
		 rewindSpeed:1
		
	});
});

