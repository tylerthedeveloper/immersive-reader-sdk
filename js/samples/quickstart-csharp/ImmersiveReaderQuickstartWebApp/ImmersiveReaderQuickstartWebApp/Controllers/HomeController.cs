using ImmersiveReaderQuickstartWebApp.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Configuration;
using Microsoft.IdentityModel.Clients.ActiveDirectory;
using Newtonsoft.Json.Serialization;
using System;
using System.Threading.Tasks;

namespace ImmersiveReaderQuickstartWebApp.Controllers
{
	public class HomeController : Controller
	{
		private readonly AzureActiveDirectoryTokenHelper _tokenHelper;
		private readonly string _subdomain;

		public HomeController(IConfiguration configuration)
		{
			_subdomain = configuration[$"Subdomain"];

			if (string.IsNullOrWhiteSpace(_subdomain))
			{
				throw new ArgumentException($"{nameof(_subdomain)} is empty! Did you add it to secrets.json? See ReadMe.txt.");
			}

			string tenantId = configuration[$"TenantId"];
			string clientId = configuration[$"ClientId"];
			string clientSecret = configuration[$"ClientSecret"];

			_tokenHelper = new AzureActiveDirectoryTokenHelper(tenantId, clientId, clientSecret);
		}

		public IActionResult Index()
		{
			return View();
		}

		[HttpGet]
		public async Task<JsonResult> TokenAndSubdomain()
		{
			try
			{
				string tokenResult = await _tokenHelper.GetTokenAsync();

				return new JsonResult(new { token = tokenResult, subdomain = _subdomain });
			}
			catch
			{
				return new JsonResult(new { error = "Unable to acquire Azure AD token." });
			}
		}
	}
}
