var main = function() {
    $('body').scrollspy();

    $('#intro-link').smoothScroll({
        offset: -176
    });
    $('#features-link').smoothScroll({
        offset: -90
    });
    $('#download-link').smoothScroll({
        offset: -90
    });
    $('#contact-link').smoothScroll({
        offset: -90
    });
};

$(document).ready(main);
